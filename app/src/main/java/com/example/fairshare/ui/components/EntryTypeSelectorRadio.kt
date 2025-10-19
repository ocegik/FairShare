package com.example.fairshare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EntryTypeSelectorRadio(
    onTypeSelected: (String) -> Unit
) {
    val options = listOf("Income", "Expense")
    var selectedType by remember { mutableStateOf(options.first()) }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    selectedType = option
                    onTypeSelected(option)
                }
            ) {
                RadioButton(
                    selected = selectedType == option,
                    onClick = {
                        selectedType = option
                        onTypeSelected(option)
                    }
                )
                Text(option)
            }
        }
    }
}

