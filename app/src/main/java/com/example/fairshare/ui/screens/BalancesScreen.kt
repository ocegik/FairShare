package com.example.fairshare.ui.screens

import android.util.Log
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.R
import com.example.fairshare.core.data.models.DebtData
import com.example.fairshare.ui.components.DebtCard
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtOperation
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.UserViewModel


@Composable
fun BalancesScreen(
    navController: NavHostController,
    debtViewModel: DebtViewModel,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel
) {
    val userId by authViewModel.currentUserId.collectAsState()
    val userGroups by groupViewModel.userGroups.collectAsState()
    val debts by debtViewModel.debts.collectAsState()
    val isLoading by debtViewModel.isLoading.collectAsState()


    var selectedTab by remember { mutableIntStateOf(0) } // 0 = You Owe, 1 = You're Owed
    var selectedGroupId by remember { mutableStateOf<String?>(null) }

    var debtToSettle by remember { mutableStateOf<DebtData?>(null) }
    var showSettleDialog by remember { mutableStateOf(false) }

    var refreshTrigger by remember { mutableIntStateOf(0) }

    // Load user's groups
    LaunchedEffect(userId) {
        if (userId != null) {
            groupViewModel.loadInitialUserGroups(userId!!)
        }
    }

    // Load debts when group or tab changes
    LaunchedEffect(userId, selectedGroupId, selectedTab, refreshTrigger) {
        if (userId != null) {
            if (selectedGroupId != null) {
                // This loads ALL debts for the group (Mixed In/Out)
                debtViewModel.loadDebtsByGroup(selectedGroupId!!)
            } else {
                // This loads specific lists
                if (selectedTab == 0) {
                    debtViewModel.loadDebtsOwedByUser(userId!!)
                } else {
                    debtViewModel.loadDebtsOwedToUser(userId!!)
                }
            }
        }
    }

    val displayedDebts = remember(debts, selectedTab, userId, selectedGroupId) {
        debts.filter { debt ->
            val isOwedByUser = debt.fromUserId == userId
            val isOwedToUser = debt.toUserId == userId

            // Only show active (pending) debts or recently settled ones,
            // depending on your preference. Here we show all returned by VM
            // but filtered by direction.
            when (selectedTab) {
                0 -> isOwedByUser // Tab: You Owe
                1 -> isOwedToUser // Tab: You are Owed
                else -> false
            }
        }
    }

    if (showSettleDialog && debtToSettle != null) {
        AlertDialog(
            onDismissRequest = {
                showSettleDialog = false
                debtToSettle = null
            },
            title = { Text("Confirm Settlement") },
            text = {
                Text("Mark debt of ₹${String.format("%.2f", debtToSettle!!.amount)} as paid?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        debtToSettle?.let { debt ->
                            debtViewModel.settleDebt(
                                debtId = debt.id,
                                // We don't pass the reload params here anymore,
                                // we handle reload in the UI callback below
                                onComplete = { success ->
                                    if (success) {
                                        userViewModel.updateStatsForDebt(debt, DebtOperation.DEBT_SETTLED)
                                        // Increment trigger to force LaunchedEffect to reload data
                                        refreshTrigger++
                                    }
                                }
                            )
                        }
                        showSettleDialog = false
                        debtToSettle = null
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSettleDialog = false
                        debtToSettle = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = stringResource(R.string.debts_and_receivables),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Group Filter
        if (userGroups.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGroupId == null,
                    onClick = { selectedGroupId = null },
                    label = { Text("All Groups") }
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
                text = { Text(stringResource(R.string.you_owe)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.you_are_owed)) }
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
        } else if (displayedDebts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0)
                        stringResource(R.string.no_debts_owed)
                    else
                        stringResource(R.string.no_debts_to_collect),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedDebts, key = { it.id }) { debt ->
                    // Helper to fetch names safely
                    val fromName by produceState(initialValue = "Loading...") {
                        value = if (debt.fromUserId == userId) {
                            "You"
                        } else {
                            groupViewModel.getMemberName(debt.groupId ?: "", debt.fromUserId)
                        }
                    }

                    val toName by produceState(initialValue = "Loading...") {
                        value = if (debt.toUserId == userId) {
                            "You"
                        } else {
                            groupViewModel.getMemberName(debt.groupId ?: "", debt.toUserId)
                        }
                    }

                    DebtCard(
                        debt = debt,
                        fromName = fromName,
                        toName = toName,
                        currentUserId = userId ?: "",
                        // Only allow settling if the user is the one getting paid (Tab 1)
                        // AND the debt is not already settled
                        canSettle = (selectedTab == 1) && (debt.status == "pending"),
                        onSettle = {
                            debtToSettle = debt
                            showSettleDialog = true
                        }
                    )
                }
            }
        }
    }
}