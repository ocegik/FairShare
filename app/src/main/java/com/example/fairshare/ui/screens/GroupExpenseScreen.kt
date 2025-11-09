package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.ui.components.BackButton
import com.example.fairshare.ui.components.ExpenseFormScreen
import com.example.fairshare.ui.components.GroupSelector
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun GroupExpenseScreen(navController: NavHostController,
                       expenseViewModel: ExpenseViewModel,
                       authViewModel: AuthViewModel,
                       userViewModel: UserViewModel,
                       groupViewModel: GroupViewModel
) {

    val userId by authViewModel.currentUserId.collectAsState()

    LaunchedEffect(userId) {
        if (userId != null) {
            groupViewModel.loadInitialUserGroups(userId!!)
        }
    }

    val userGroups by groupViewModel.userGroups.collectAsState()
    val bookmarkedGroupId by userViewModel.bookmarkedGroupId.collectAsState()

    var selectedGroupId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userGroups, bookmarkedGroupId) {
        val fallback = userGroups.firstOrNull()?.groupId
        selectedGroupId = bookmarkedGroupId.takeIf { !it.isNullOrBlank() } ?: fallback
    }


    val members = remember(selectedGroupId, userGroups) {
        selectedGroupId?.let { gid ->
            userGroups.find { it.groupId == gid }?.members ?: emptyList()
        } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp) // more balanced padding
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            BackButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "Group Expense",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        GroupSelector(
            groups = userGroups,
            selectedGroupId = selectedGroupId,
            onGroupSelected = { id -> selectedGroupId = id }
        )

        if (selectedGroupId == null) {
            Text("Select a group to add expenses.")
            return@Column
        }

        ExpenseFormScreen(
            navController = navController,
            isGroupExpense = true,
            expenseViewModel = expenseViewModel,
            authViewModel = authViewModel,
            userViewModel = userViewModel,
            groupId = selectedGroupId,
            members = members)
    }
}