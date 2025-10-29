package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.fairshare.data.models.DebtData
import com.example.fairshare.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository
) : ViewModel() {

    private val _debts = MutableStateFlow<List<DebtData>>(emptyList())
    val debts: StateFlow<List<DebtData>> = _debts.asStateFlow()

    // Add a new debt
    fun addDebt(debt: DebtData) {
        debtRepository.addDebt(debt) { success ->
            if (success) {
                refreshDebtListAfterChange(debt)
            } else {
                Log.e("DebtViewModel", "Failed to add debt")
            }
        }
    }

    // Load debts where user owes others
    fun loadDebtsOwedByUser(userId: String) {
        debtRepository.getDebtsOwedByUser(userId) { list ->
            _debts.value = list
        }
    }

    // Load debts where user is owed money
    fun loadDebtsOwedToUser(userId: String) {
        debtRepository.getDebtsOwedToUser(userId) { list ->
            _debts.value = list
        }
    }

    // Load debts for a specific group
    fun loadDebtsByGroup(groupId: String) {
        debtRepository.getDebtsByGroup(groupId) { list ->
            _debts.value = list
        }
    }

    // Update debt (e.g., mark as settled)
    fun updateDebt(
        debtId: String,
        updates: Map<String, Any>,
        groupId: String? = null,
        fromUserId: String? = null,
        toUserId: String? = null
    ) {
        debtRepository.updateDebt(debtId, updates) { success ->
            if (!success) {
                Log.e("DebtViewModel", "Failed to update debt")
                return@updateDebt
            }
            // Refresh based on context
            when {
                groupId != null -> loadDebtsByGroup(groupId)
                fromUserId != null -> loadDebtsOwedByUser(fromUserId)
                toUserId != null -> loadDebtsOwedToUser(toUserId)
            }
        }
    }

    // Delete debt (optional)
    fun deleteDebt(
        debtId: String,
        groupId: String? = null,
        fromUserId: String? = null,
        toUserId: String? = null
    ) {
        debtRepository.deleteDebt(debtId) { success ->
            if (!success) {
                Log.e("DebtViewModel", "Failed to delete debt")
                return@deleteDebt
            }
            // Refresh after deletion
            when {
                groupId != null -> loadDebtsByGroup(groupId)
                fromUserId != null -> loadDebtsOwedByUser(fromUserId)
                toUserId != null -> loadDebtsOwedToUser(toUserId)
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
}