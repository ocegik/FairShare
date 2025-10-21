package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialogDefaults.MinHeightForTimePicker
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Initialize with current time
    val currentCalendar = Calendar.getInstance()
    val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = currentCalendar.get(Calendar.MINUTE)

    val state = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        is24Hour = false
    )

    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }
    val configuration = LocalConfiguration.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select a Time",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (configuration.screenHeightDp > 400) {
                        IconButton(
                            onClick = {
                                displayMode = if (displayMode == TimePickerDisplayMode.Picker) {
                                    TimePickerDisplayMode.Input
                                } else {
                                    TimePickerDisplayMode.Picker
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (displayMode == TimePickerDisplayMode.Picker) {
                                    Icons.Default.Keyboard
                                } else {
                                    Icons.Default.Schedule
                                },
                                contentDescription = "Toggle time picker mode"
                            )
                        }
                    }
                }

                if (
                    displayMode == TimePickerDisplayMode.Picker &&
                    configuration.screenHeightDp.dp > MinHeightForTimePicker
                ) {
                    TimePicker(state = state)
                } else {
                    TimeInput(state = state)
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        // Always return a valid time (selected or current)
                        onTimeSelected(state.hour, state.minute)
                        onDismiss()
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}