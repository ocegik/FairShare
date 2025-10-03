package com.example.fairshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    object Home : Destination("home", "Home", Icons.Default.Home, "Home Screen")
    object Expense : Destination("expense", "Expense", Icons.Default.Add, "Add Screen")
    object Stats : Destination("stats", "Stats", Icons.Default.Star, "Stats Screen")

    companion object {
        val entries = listOf(Home, Expense, Stats)
    }
}
