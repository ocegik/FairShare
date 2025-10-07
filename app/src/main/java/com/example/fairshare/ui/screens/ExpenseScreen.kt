package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.fairshare.ui.components.LargeFloatingActionButtonSample

@Composable
fun ExpenseScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Expense Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        LargeFloatingActionButtonSample()
    }
}
