package com.example.fairshare.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
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
import com.example.fairshare.navigation.Screen
import com.example.fairshare.ui.components.DebtCard
import com.example.fairshare.ui.components.DebtOperation
import com.example.fairshare.viewmodel.AuthViewModel
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
            when (selectedTab) {
                0 -> isOwedByUser
                1 -> isOwedToUser
                else -> false
            }
        }
            // Sort by date (Newest first)
            // Ensure your DebtData has a 'createdAt' or 'timestamp' field!
            .sortedByDescending { it.createdAt }
    }

    val (pendingDebts, settledDebts) = remember(displayedDebts) {
        displayedDebts.partition { it.status == "pending" }
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
                                onComplete = { success ->
                                    if (success) {
                                        userViewModel.updateStatsForDebt(debt, DebtOperation.DEBT_SETTLED)

                                        val otherUserId = if (debt.fromUserId == userId) debt.toUserId else debt.fromUserId
                                        userViewModel.updatePeerStats(otherUserId, debt, DebtOperation.DEBT_SETTLED)

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateDebt.route) }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp)
                ) {

                    if (pendingDebts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(pendingDebts, key = { it.id }) { debt ->
                            DebtListItemWrapper(
                                debt = debt,
                                userId = userId,
                                groupViewModel = groupViewModel,
                                selectedTab = selectedTab,
                                onSettleRequest = { d ->
                                    debtToSettle = d
                                    showSettleDialog = true
                                }
                            )
                        }
                    }

                    if (settledDebts.isNotEmpty()) {
                        item {
                            if (pendingDebts.isNotEmpty()) Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "History",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(settledDebts, key = { it.id }) { debt ->
                            DebtListItemWrapper(
                                debt = debt,
                                userId = userId,
                                groupViewModel = groupViewModel,
                                selectedTab = selectedTab,
                                onSettleRequest = { /* Cannot settle history */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DebtListItemWrapper(
    debt: DebtData,
    userId: String?,
    groupViewModel: GroupViewModel,
    selectedTab: Int,
    onSettleRequest: (DebtData) -> Unit
) {
    val fromName by produceState(initialValue = "...") {
        value = if (debt.fromUserId == userId) "You"
        else groupViewModel.getMemberName(debt.groupId ?: "", debt.fromUserId)
    }

    val toName by produceState(initialValue = "...") {
        value = if (debt.toUserId == userId) "You"
        else groupViewModel.getMemberName(debt.groupId ?: "", debt.toUserId)
    }

    DebtCard(
        debt = debt,
        fromName = fromName,
        toName = toName,
        currentUserId = userId ?: "",
        canSettle = (selectedTab == 1) && (debt.status == "pending"),
        onSettle = { onSettleRequest(debt) }
    )
}