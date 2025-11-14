package com.example.fairshare.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fairshare.data.models.ExpenseData
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormScreen(
    navController: NavHostController,
    isGroupExpense: Boolean = false,
    expenseViewModel: ExpenseViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    debtViewModel: DebtViewModel,
    groupId: String? = null,
    members: List<String> = emptyList()
) {

    val userId by authViewModel.currentUserId.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var entryType by remember { mutableStateOf(if (isGroupExpense) "Expense" else "Income") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }

    // Group-specific state
    val allPeople = members
    var selectedPeople by remember { mutableStateOf(allPeople) }

    LaunchedEffect(entryType) {
        category = ""
    }

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        TitleField(title) { title = it }
        AmountField(amount) { amount = it }


        if (!isGroupExpense) {
            EntryTypeSelectorRadio { selected -> entryType = selected }
        }

        OutlinedButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Text(
                text = category.ifBlank { "Select Category" },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Group-specific people selector
        if (isGroupExpense) {
            ExpensePeopleSelector(
                people = allPeople,
                onSelectionChange = { selectedPeople = it }
            )
        }

        NoteField(note) { note = it }

        // Date Picker Button
        DatePickerButton(selectedDateMillis)
        { showDatePicker = true }

        // Time Picker Button
        TimePickerButton(selectedHour, selectedMinute)
        { showTimePicker = true }

        // Date Picker Dialog
        if (showDatePicker) {
            CustomDatePickerDialog(
                onDateSelected = { millis -> selectedDateMillis = millis },
                onDismiss = { showDatePicker = false }
            )
        }

        // Time Picker Dialog
        if (showTimePicker) {
            CustomTimePickerDialog(
                onTimeSelected = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                },
                onDismiss = { showTimePicker = false }
            )
        }

        // Submit Button
        Button(
            onClick = {
                Log.d("ExpenseForm", "=== SUBMIT BUTTON CLICKED ===")

                val currentUserId = userId ?: run {
                    Log.e("ExpenseForm", "User not logged in")
                    return@Button
                }

                val isValid = if (isGroupExpense) {
                    title.isNotBlank() && amount.isNotBlank() &&
                            category.isNotBlank() && selectedPeople.isNotEmpty()
                } else {
                    title.isNotBlank() && amount.isNotBlank() &&
                            category.isNotBlank()
                }

                if (isValid) {
                    val mergedDateTime = mergeDateAndTime(
                        selectedDateMillis,
                        selectedHour,
                        selectedMinute
                    )

                    val expense = ExpenseData(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        amount = amount.toDouble(),
                        category = category,
                        note = note,
                        entryType = entryType,
                        dateTime = mergedDateTime,
                        userId = currentUserId,
                        groupId = if (isGroupExpense) groupId else null,
                        participants = if (isGroupExpense) selectedPeople else null,
                        paidBy = if (isGroupExpense) currentUserId else null
                    )

                    expenseViewModel.addExpense(expense)
                    userViewModel.updateStatsForExpense(expense)

                    if (isGroupExpense && groupId != null && selectedPeople.isNotEmpty()) {
                        // Launch coroutine to create debts and wait for completion
                        CoroutineScope(Dispatchers.Main).launch {
                            val success = createDebtsForGroupExpense(
                                expenseId = expense.id,
                                amount = expense.amount,
                                paidBy = currentUserId,
                                participants = selectedPeople,
                                groupId = groupId,
                                debtViewModel = debtViewModel
                            )
                            // Navigate back after debts are created
                            navController.popBackStack()
                        }
                    } else {
                        // For non-group expenses, navigate immediately
                        navController.popBackStack()
                    }
                } else {
                    Log.e("ExpenseForm", "Validation failed - expense not created")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Save Expense", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            CategoryBottomSheet(
                entryType = entryType,
                onCategorySelected = { selectedCategory ->
                    category = selectedCategory
                    showBottomSheet = false
                }
            )
        }
    }
}