package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.UserViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormScreen(
    navController: NavHostController,
    isGroupExpense: Boolean = false,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {

    val user by userViewModel.user.collectAsState()
    val currentUserId = user?.get("id") as? String ?: ""
    val currentUserName = user?.get("name") as? String ?: ""

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var entryType by remember { mutableStateOf("Expense") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }

    // Group-specific state
    val allPeople = listOf("Tarun", "Mohit", "Pramod", "Pandu", "Ankit")
    var selectedPeople by remember { mutableStateOf(allPeople) }

    LaunchedEffect(entryType) {
        category = ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            TitleField(title) { title = it }
            AmountField(amount) { amount = it }


            if (!isGroupExpense) {
                EntryTypeSelectorRadio { selected -> entryType = selected }
            }

            OutlinedButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = category.ifBlank { "Select Category" },
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select category"
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
                    val isValid = if (isGroupExpense) {
                        title.isNotBlank() && amount.isNotBlank() &&
                                category.isNotBlank() && selectedPeople.isNotEmpty()
                    } else {
                        title.isNotBlank() && amount.isNotBlank()
                    }
                    //&& category.isNotBlank()
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
                            groupId = if (isGroupExpense) "groupIdHere" else null,
                            participants = if (isGroupExpense) selectedPeople else null,
                            paidBy = if (isGroupExpense) "currentUserIdHere" else null
                        )

                        expenseViewModel.addExpense(expense)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Expense", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
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