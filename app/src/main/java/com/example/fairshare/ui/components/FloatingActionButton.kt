package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun LargeFloatingActionButton(onClick: () -> Unit) {
    LargeFloatingActionButton(onClick = onClick) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Localized description",
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
        )
    }
}
