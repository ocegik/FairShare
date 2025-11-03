package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.DebtData
import com.example.fairshare.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository
) : ViewModel() {

    companion object {
        private const val TAG = "DebtViewModel"
    }
    private val _debts = MutableStateFlow<List<DebtData>>(emptyList())
    val debts: StateFlow<List<DebtData>> = _debts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Add a new debt
    fun addDebt(debt: DebtData, onComplete: (Boolean) -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            debtRepository.addDebt(debt)
                .onSuccess {
                    refreshDebtListAfterChange(debt)
                    Log.d(TAG, "Debt added successfully")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to add debt", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    // Load debts where user owes others
    fun loadDebtsOwedByUser(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            debtRepository.getDebtsOwedByUser(userId)
                .onSuccess { list ->
                    _debts.value = list
                    Log.d(TAG, "Loaded ${list.size} debts owed by user: $userId")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load debts owed by user: $userId", exception)
                    _debts.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    // Load debts where user is owed money
    fun loadDebtsOwedToUser(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            debtRepository.getDebtsOwedToUser(userId)
                .onSuccess { list ->
                    _debts.value = list
                    Log.d(TAG, "Loaded ${list.size} debts owed to user: $userId")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load debts owed to user: $userId", exception)
                    _debts.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    // Load debts for a specific group
    fun loadDebtsByGroup(groupId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            debtRepository.getDebtsByGroup(groupId)
                .onSuccess { list ->
                    _debts.value = list
                    Log.d(TAG, "Loaded ${list.size} debts for group: $groupId")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load debts for group: $groupId", exception)
                    _debts.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    // Update debt (e.g., mark as settled)
    fun updateDebt(
        debtId: String,
        updates: Map<String, Any>,
        groupId: String? = null,
        fromUserId: String? = null,
        toUserId: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            debtRepository.updateDebt(debtId, updates)
                .onSuccess {
                    // Refresh based on context
                    when {
                        groupId != null -> loadDebtsByGroup(groupId)
                        fromUserId != null -> loadDebtsOwedByUser(fromUserId)
                        toUserId != null -> loadDebtsOwedToUser(toUserId)
                    }
                    Log.d(TAG, "Debt updated successfully: $debtId")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to update debt: $debtId", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    // Delete debt (optional)
    fun deleteDebt(
        debtId: String,
        groupId: String? = null,
        fromUserId: String? = null,
        toUserId: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            debtRepository.deleteDebt(debtId)
                .onSuccess {
                    // Refresh after deletion
                    when {
                        groupId != null -> loadDebtsByGroup(groupId)
                        fromUserId != null -> loadDebtsOwedByUser(fromUserId)
                        toUserId != null -> loadDebtsOwedToUser(toUserId)
                    }
                    Log.d(TAG, "Debt deleted successfully: $debtId")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to delete debt: $debtId", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    // Internal helper to reload correct list after adding a new debt
    private fun refreshDebtListAfterChange(debt: DebtData) {
        when {
            debt.groupId != null -> loadDebtsByGroup(debt.groupId!!)
            debt.fromUserId.isNotEmpty() -> loadDebtsOwedByUser(debt.fromUserId)
            debt.toUserId.isNotEmpty() -> loadDebtsOwedToUser(debt.toUserId)
        }
    }
}