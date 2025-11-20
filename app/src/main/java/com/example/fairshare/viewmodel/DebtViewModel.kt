package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.core.data.models.DebtData
import com.example.fairshare.core.data.models.DebtSummary
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                debtRepository.addDebt(debt)
                    .onSuccess {
                        Log.d(TAG, "Debt added successfully")
                        onComplete(true)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to add debt", exception)
                        onComplete(false)
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load debts where user owes others
    fun loadDebtsOwedByUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                debtRepository.getDebtsOwedByUser(userId)
                    .onSuccess { list ->
                        _debts.value = list
                        Log.d(TAG, "Loaded ${list.size} debts owed by user: $userId")
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to load debts owed by user: $userId", exception)
                        _debts.value = emptyList()
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load debts where user is owed money
    fun loadDebtsOwedToUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                debtRepository.getDebtsOwedToUser(userId)
                    .onSuccess { list ->
                        _debts.value = list
                        Log.d(TAG, "Loaded ${list.size} debts owed to user: $userId")
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to load debts owed to user: $userId", exception)
                        _debts.value = emptyList()
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load debts for a specific group
    fun loadDebtsByGroup(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                debtRepository.getDebtsByGroup(groupId)
                    .onSuccess { list ->
                        _debts.value = list
                        Log.d(TAG, "Loaded ${list.size} debts for group: $groupId")
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to load debts for group: $groupId", exception)
                        _debts.value = emptyList()
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update debt (e.g., mark as settled)
    fun updateDebt(
        debtId: String,
        updates: Map<String, Any>,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                debtRepository.updateDebt(debtId, updates)
                    .onSuccess {
                        onComplete(true)
                    }
                    .onFailure { exception ->
                        onComplete(false)
                    }
            } finally {
                _isLoading.value = false
            }
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
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
            } finally {
                _isLoading.value = false
            }
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

    fun getGroupDebtBreakdown(
        groupId: String,
        onResult: (List<DebtSummary>) -> Unit
    ) {
        viewModelScope.launch {
            debtRepository.getDebtsByGroup(groupId)
                .onSuccess { allDebts ->
                    val pendingDebts = allDebts.filter { it.status == "pending" }

                    // Group debts by from-to pair
                    val debtMap = mutableMapOf<Pair<String, String>, Double>()

                    pendingDebts.forEach { debt ->
                        val key = Pair(debt.fromUserId, debt.toUserId)
                        debtMap[key] = (debtMap[key] ?: 0.0) + debt.amount
                    }

                    // Convert to list of summaries
                    val summaries = debtMap.map { (pair, amount) ->
                        DebtSummary(
                            fromUserId = pair.first,
                            toUserId = pair.second,
                            totalAmount = amount
                        )
                    }

                    Log.d(TAG, "Group $groupId debt breakdown: ${summaries.size} relationships")
                    onResult(summaries)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to get group debt breakdown", exception)
                    onResult(emptyList())
                }
        }
    }

    fun getDebtSummaryForUserInGroup(
        userId: String,
        groupId: String,
        onResult: (totalOwed: Double, totalOwing: Double) -> Unit
    ) {
        viewModelScope.launch {
            // Get all debts for the group
            debtRepository.getDebtsByGroup(groupId)
                .onSuccess { allDebts ->
                    val pendingDebts = allDebts.filter { it.status == "pending" }

                    // Calculate what user owes
                    val totalOwed = pendingDebts
                        .filter { it.fromUserId == userId }
                        .sumOf { it.amount }

                    // Calculate what user is owed
                    val totalOwing = pendingDebts
                        .filter { it.toUserId == userId }
                        .sumOf { it.amount }

                    Log.d(TAG, "Debt summary for user $userId in group $groupId:")
                    Log.d(TAG, "  - Owes: $$totalOwed")
                    Log.d(TAG, "  - Is owed: $$totalOwing")
                    Log.d(TAG, "  - Net: $${totalOwing - totalOwed}")

                    onResult(totalOwed, totalOwing)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to get debt summary", exception)
                    onResult(0.0, 0.0)
                }
        }
    }

    fun settleDebt(
        debtId: String,
        // Removed group/user params as we rely on UI to trigger refresh
        onComplete: (Boolean) -> Unit = {}
    ) {
        val updates = mapOf(
            "status" to "settled",
            "settledAt" to System.currentTimeMillis()
        )

        // Call the simplified update function
        updateDebt(debtId, updates, onComplete)
    }
}