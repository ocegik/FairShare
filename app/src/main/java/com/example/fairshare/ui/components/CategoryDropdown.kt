package com.example.fairshare.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Food", "Travel", "Shopping", "Bills", "Other")
    var expanded by remember { mutableStateOf(false) }

    Log.d("CategoryDropdown", "Expanded state: $expanded")
    Log.d("CategoryDropdown", "Selected category: $selectedCategory")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Log.d("CategoryDropdown", "TextField clicked!")
                    expanded = true
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            Log.d("CategoryDropdown", "DropdownMenu is composing with ${categories.size} items")
            categories.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        Log.d("CategoryDropdown", "Item clicked: $option")
                        onCategorySelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
