package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.fairshare.repository.ExpenseRepository
import com.example.fairshare.data.models.ExpenseData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<ExpenseData>>(emptyList())
    val expenses: StateFlow<List<ExpenseData>> = _expenses.asStateFlow()

    fun addExpense(expense: ExpenseData) {

        expenseRepository.addExpense(expense) { success ->

            if (success) {
                val groupId = expense.groupId
                val userId = expense.userId

                if (groupId != null) {
                    loadExpensesByGroup(groupId)
                } else {
                    loadExpensesByUser(userId)
                }
            } else {
                Log.e("ExpenseViewModel", "Failed to add expense")
            }
        }
    }

    fun loadExpensesByUser(userId: String) {
        expenseRepository.getExpensesByUser(userId) { list ->
            _expenses.value = list
        }
    }

    fun loadExpensesByGroup(groupId: String) {
        expenseRepository.getExpensesByGroup(groupId) { list ->
            _expenses.value = list
        }
    }

    fun updateExpense(
        expenseId: String,
        updates: Map<String, Any>,
        userId: String? = null,
        groupId: String? = null
    ) {
        expenseRepository.updateExpense(expenseId, updates) { success ->
            if (!success) return@updateExpense
            // Reload depending on context
            when {
                groupId != null -> loadExpensesByGroup(groupId)
                userId != null -> loadExpensesByUser(userId)
            }
        }
    }

    fun deleteExpense(expenseId: String, userId: String? = null, groupId: String? = null) {
        expenseRepository.deleteExpense(expenseId) { success ->
            if (success) {
                if (groupId != null) loadExpensesByGroup(groupId)
                else if (userId != null) loadExpensesByUser(userId)
            }
        }
    }
}