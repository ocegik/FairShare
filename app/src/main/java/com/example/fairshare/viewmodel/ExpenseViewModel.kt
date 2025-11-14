package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.repository.ExpenseRepository
import com.example.fairshare.core.data.models.ExpenseData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ExpenseViewModel"
    }

    private val _expenses = MutableStateFlow<List<ExpenseData>>(emptyList())
    val expenses: StateFlow<List<ExpenseData>> = _expenses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addExpense(expense: ExpenseData, onComplete: (Boolean) -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            expenseRepository.addExpense(expense)
                .onSuccess {
                    val groupId = expense.groupId
                    val userId = expense.userId

                    if (groupId != null) {
                        loadExpensesByGroup(groupId)
                    } else {
                        loadExpensesByUser(userId)
                    }
                    Log.d(TAG, "Expense added successfully")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to add expense", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    fun loadExpensesByUser(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            expenseRepository.getExpensesByUser(userId)
                .onSuccess { list ->
                    _expenses.value = list
                    Log.d(TAG, "Loaded ${list.size} expenses for user: $userId")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load expenses for user: $userId", exception)
                    _expenses.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    fun loadExpensesByGroup(groupId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            expenseRepository.getExpensesByGroup(groupId)
                .onSuccess { list ->
                    _expenses.value = list
                    Log.d(TAG, "Loaded ${list.size} expenses for group: $groupId")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load expenses for group: $groupId", exception)
                    _expenses.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    fun updateExpense(
        expenseId: String,
        updates: Map<String, Any>,
        userId: String? = null,
        groupId: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            expenseRepository.updateExpense(expenseId, updates)
                .onSuccess {
                    // Reload depending on context
                    when {
                        groupId != null -> loadExpensesByGroup(groupId)
                        userId != null -> loadExpensesByUser(userId)
                    }
                    Log.d(TAG, "Expense updated successfully: $expenseId")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to update expense: $expenseId", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    fun deleteExpense(
        expenseId: String,
        userId: String? = null,
        groupId: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId)
                .onSuccess {
                    when {
                        groupId != null -> loadExpensesByGroup(groupId)
                        userId != null -> loadExpensesByUser(userId)
                    }
                    Log.d(TAG, "Expense deleted successfully: $expenseId")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to delete expense: $expenseId", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }
}