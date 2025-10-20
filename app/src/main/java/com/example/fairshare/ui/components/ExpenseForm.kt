package com.example.fairshare.ui.components

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormScreen(
    navController: NavHostController,
    isGroupExpense: Boolean = false
) {
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
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

        // Show CategoryDropdown first for personal, after EntryType for group
        if (!isGroupExpense) {
            EntryTypeSelectorRadio { selected -> entryType = selected }
            CategoryDropdown(selectedCategory = category, onCategorySelected = { category = it })
        } else {
            CategoryDropdown(selectedCategory = category, onCategorySelected = { category = it })
            EntryTypeSelectorRadio { selected -> entryType = selected }
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
                    title.isNotBlank() && amount.isNotBlank() && category.isNotBlank()
                }

                if (isValid) {
                    val mergedDateTime = mergeDateAndTime(
                        selectedDateMillis,
                        selectedHour,
                        selectedMinute
                    )

                    val expense = ExpenseData(
                        title = title,
                        amount = amount.toDouble(),
                        category = category,
                        note = note,
                        entryType = entryType,
                        dateTime = mergedDateTime
                    )
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
    }
}