package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.ExpenseData
import com.example.fairshare.data.models.Group
import com.example.fairshare.repository.ExpenseRepository
import com.example.fairshare.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth

) : ViewModel() {

    private val _personalExpenses = MutableStateFlow<List<ExpenseData>>(emptyList())
    val personalExpenses = _personalExpenses.asStateFlow()

    private val _groupExpenses = MutableStateFlow<List<ExpenseData>>(emptyList())
    val groupExpenses = _groupExpenses.asStateFlow()

    private val _yourExpenses = MutableStateFlow<List<ExpenseData>>(emptyList())
    val yourExpenses = _yourExpenses.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow()

    init { loadAll() }

    private fun loadAll() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {

            // Yours
            _yourExpenses.value =
                expenseRepository.getExpensesByUser(userId).getOrElse { emptyList() }

            // Personal
            _personalExpenses.value =
                _yourExpenses.value.filter { it.groupId == null }

            // Groups
            val groupIds =
                userRepository.getUserGroups(userId).getOrElse { emptyList() }

            val allGroupExpenses = mutableListOf<ExpenseData>()

            groupIds.forEach { gid ->
                expenseRepository.getExpensesByGroup(gid)
                    .onSuccess { allGroupExpenses += it }
            }

            _groupExpenses.value = allGroupExpenses

            _loading.value = false
        }
    }
}