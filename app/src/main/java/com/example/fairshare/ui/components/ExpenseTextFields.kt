package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun TitleField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Title") },
        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AmountField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { ch -> ch.isDigit() }) onChange(it) },
        label = { Text("Amount (₹)") },
        leadingIcon = { Icon(Icons.Filled.CurrencyRupee, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NoteField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Note (optional)") },
        leadingIcon = { Icon(Icons.Filled.AddLink, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}
