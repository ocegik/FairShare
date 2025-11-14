package com.example.fairshare.ui.components

import android.os.Build
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DatePickerButton(
    selectedDateMillis: Long?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        val label = if (selectedDateMillis != null) {
            val date = Instant.ofEpochMilli(selectedDateMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            "Selected: $date"
        } else {
            val today = LocalDate.now().toString()
            "Selected: $today"
        }

        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

// Extract time picker button as reusable composable
@Composable
fun TimePickerButton(
    selectedHour: Int?,
    selectedMinute: Int?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val text = if (selectedHour != null && selectedMinute != null) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, selectedHour)
            cal.set(Calendar.MINUTE, selectedMinute)
            "Selected: ${formatter.format(cal.time)}"
        } else {
            "Selected: ${formatter.format(Date())}"
        }

        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

