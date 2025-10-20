package com.example.fairshare.ui.screens

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
import com.example.fairshare.ui.components.AmountField
import com.example.fairshare.ui.components.CategoryDropdown
import com.example.fairshare.ui.components.CustomDatePickerDialog
import com.example.fairshare.ui.components.CustomTimePickerDialog
import com.example.fairshare.ui.components.EntryTypeSelectorRadio
import com.example.fairshare.ui.components.ExpenseData
import com.example.fairshare.ui.components.NoteField
import com.example.fairshare.ui.components.TitleField
import com.example.fairshare.ui.components.mergeDateAndTime
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalExpenseScreen(
    navController: NavHostController
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var entryType by remember { mutableStateOf("Expense")}
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }


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
        EntryTypeSelectorRadio{selected ->
        entryType = selected}
        CategoryDropdown(selectedCategory = category, onCategorySelected = { category = it })

        NoteField(note) { note = it }

        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )  {
            Text(
                text = if (selectedDateMillis != null) {
                    val date = Instant.ofEpochMilli(selectedDateMillis!!)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    "Selected: $date"
                } else {
                    // Show current date when nothing is selected
                    val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDate.now().toString()
                    } else {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        sdf.format(Date())
                    }
                    "Selected: $currentDate"
                }
            )
        }

        Button(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (selectedHour != null && selectedMinute != null) {
                    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, selectedHour!!)
                    cal.set(Calendar.MINUTE, selectedMinute!!)
                    "Selected: ${formatter.format(cal.time)}"
                } else {
                    // Show current time when nothing is selected
                    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val currentTime = Calendar.getInstance().time
                    "Selected: ${formatter.format(currentTime)}"
                }
            )
        }


        // 🗓️ Show your custom dialog
        if (showDatePicker) {
            CustomDatePickerDialog(
                onDateSelected = { millis -> selectedDateMillis = millis },
                onDismiss = { showDatePicker = false }
            )
        }
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
                if (title.isNotBlank() && amount.isNotBlank() && category.isNotBlank() ) {
                    val mergedDateTime = mergeDateAndTime(
                        selectedDateMillis,
                        selectedHour,
                        selectedMinute
                    )


                    val expense = ExpenseData(title = title,
                        entryType = entryType,
                        amount = amount.toDouble(),
                        category = category,
                        note = note,
                        dateTime = mergedDateTime)
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



