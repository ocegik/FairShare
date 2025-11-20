package com.example.fairshare.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.fairshare.core.data.models.GroupMember
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.UserViewModel
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fairshare.R
import com.example.fairshare.core.data.models.DebtData
import com.example.fairshare.core.ui.AmountField
import com.example.fairshare.core.ui.NoteField
import com.example.fairshare.ui.components.CustomDatePickerDialog
import com.example.fairshare.ui.components.DatePickerButton
import com.example.fairshare.viewmodel.DebtOperation
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDebtScreen(
    navController: NavController,
    debtViewModel: DebtViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    // Pass a list of known users/friends/group members to select from
    availableUsers: List<GroupMember> = emptyList()
) {
    val currentUserId by authViewModel.currentUserId.collectAsState()

    // State
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    // true = I Lent (They owe me), false = I Borrowed (I owe them)
    var isLending by remember { mutableStateOf(true) }

    // The other person involved
    var selectedUser by remember { mutableStateOf<GroupMember?>(null) }
    var showUserSheet by remember { mutableStateOf(false) }

    // Date Logic (Reusing your existing logic)
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Assuming you have a BackButton component like in PersonalExpenseScreen
            // BackButton(onClick = { navController.popBackStack() })
            Text(
                text = "Add Debt Record", // Add to strings.xml
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // 2. Transaction Type Toggle (I Lent vs I Borrowed)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DebtTypeButton(
                text = "I Lent",
                isSelected = isLending,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            ) { isLending = true }

            DebtTypeButton(
                text = "I Borrowed",
                isSelected = !isLending,
                color = MaterialTheme.colorScheme.error, // Or a warning color
                modifier = Modifier.weight(1f)
            ) { isLending = false }
        }

        // 3. Person Selector
        Text(
            text = if (isLending) "Who did you lend to?" else "Who did you borrow from?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedButton(
            onClick = { showUserSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            val name = selectedUser?.displayName ?: selectedUser?.email ?: "Select Person"

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedUser == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        // 4. Amount Input
        // Reusing your existing component
        AmountField(amount) { amount = it }

        // 5. Note Input
        // Reusing your existing component
        NoteField(note) { note = it }

        // 6. Date Picker
        DatePickerButton(selectedDateMillis) { showDatePicker = true }

        if (showDatePicker) {
            CustomDatePickerDialog(
                onDateSelected = { millis -> selectedDateMillis = millis },
                onDismiss = { showDatePicker = false }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 7. Submit Button
        Button(
            onClick = {
                if (amount.isNotBlank() && selectedUser != null && currentUserId != null) {
                    val debtAmount = amount.toDoubleOrNull() ?: 0.0
                    val targetUser = selectedUser!!
                    val myId = currentUserId!!

                    // Determine direction
                    val fromId = if (isLending) myId else targetUser.uid
                    val toId = if (isLending) targetUser.uid else myId

                    val debt = DebtData(
                        id = UUID.randomUUID().toString(),
                        groupId = null, // Personal debt, usually null or a special "Non-Group" ID
                        fromUserId = fromId,
                        toUserId = toId,
                        amount = debtAmount,
                        createdAt = selectedDateMillis ?: System.currentTimeMillis(),
                        status = "pending"
                    )

                    // 1. Add Debt to Repo
                    debtViewModel.addDebt(debt) { success ->
                        if (success) {
                            // 2. Update My Stats
                            userViewModel.updateStatsForDebt(debt, DebtOperation.DEBT_ADDED)

                            // 3. Update Peer Stats (The other person)
                            userViewModel.updatePeerStats(
                                targetUserId = if (myId == fromId) toId else fromId,
                                debt = debt,
                                operation = DebtOperation.DEBT_ADDED
                            )

                            navController.popBackStack()
                        } else {
                            Log.e("CreateDebt", "Failed to add debt")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = amount.isNotBlank() && selectedUser != null
        ) {
            Text("Save Debt Record", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    // Bottom Sheet for User Selection
    if (showUserSheet) {
        ModalBottomSheet(
            onDismissRequest = { showUserSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    "Select Person",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                // Render list of available users
                if (availableUsers.isEmpty()) {
                    Text(
                        "No contacts found",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    availableUsers.forEach { user ->
                        androidx.compose.material3.ListItem(
                            headlineContent = { Text(user.displayName ?: user.email ?: "Unknown") },
                            modifier = Modifier.clickable {
                                selectedUser = user
                                showUserSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper Composable for the Toggle Buttons
@Composable
fun DebtTypeButton(
    text: String,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) color else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) color else MaterialTheme.colorScheme.onSurface

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor
        )
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}