package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.ui.components.DebtCard
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.GroupViewModel


@Composable
fun BalancesScreen(
    navController: NavHostController,
    debtViewModel: DebtViewModel,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel
) {
    val userId by authViewModel.currentUserId.collectAsState()
    val userGroups by groupViewModel.userGroups.collectAsState()
    val debts by debtViewModel.debts.collectAsState()
    val isLoading by debtViewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = You Owe, 1 = You're Owed
    var selectedGroupId by remember { mutableStateOf<String?>(null) }

    // Load user's groups
    LaunchedEffect(userId) {
        if (userId != null) {
            groupViewModel.loadInitialUserGroups(userId!!)
        }
    }

    // Load debts when group or tab changes
    LaunchedEffect(userId, selectedGroupId, selectedTab) {
        if (userId != null) {
            if (selectedGroupId != null) {
                // Load debts for specific group
                debtViewModel.loadDebtsByGroup(selectedGroupId!!)
            } else {
                // Load all user debts
                if (selectedTab == 0) {
                    debtViewModel.loadDebtsOwedByUser(userId!!)
                } else {
                    debtViewModel.loadDebtsOwedToUser(userId!!)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Debts & Receivables",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Group Filter (optional)
        if (userGroups.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGroupId == null,
                    onClick = { selectedGroupId = null },
                    label = { Text("All") }
                )
                userGroups.forEach { group ->
                    FilterChip(
                        selected = selectedGroupId == group.groupId,
                        onClick = { selectedGroupId = group.groupId },
                        label = { Text(group.name) }
                    )
                }
            }
        }

        // Tabs
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("You Owe") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("You're Owed") }
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (debts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0) "You don't owe anyone" else "Nobody owes you",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(debts.filter {
                    if (selectedGroupId != null) {
                        it.groupId == selectedGroupId
                    } else true
                }.filter {
                    if (selectedTab == 0) {
                        it.fromUserId == userId
                    } else {
                        it.toUserId == userId
                    }
                }) { debt ->
                    DebtCard(
                        debt = debt,
                        currentUserId = userId ?: "",
                        onSettle = {
                            debtViewModel.settleDebt(
                                debtId = debt.id,
                                groupId = debt.groupId,
                                onComplete = { success ->
                                    if (success) {
                                        // Optionally show success message
                                    }
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
