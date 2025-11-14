package com.example.fairshare.ui.components

import android.os.Build
import androidx.compose.foundation.layout.fillMaxWidth
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (selectedDateMillis != null) {
                val date = Instant.ofEpochMilli(selectedDateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                "Selected: $date"
            } else {
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (selectedHour != null && selectedMinute != null) {
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                cal.set(Calendar.MINUTE, selectedMinute)
                "Selected: ${formatter.format(cal.time)}"
            } else {
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val currentTime = Calendar.getInstance().time
                "Selected: ${formatter.format(currentTime)}"
            }
        )
    }
}
