package com.example.fairshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    object Home : Destination("home", "Home", Icons.Default.Home, "Home Screen")
    object Expense : Destination("expense", "Expense", Icons.Default.Add, "Add Screen")
    object Profile : Destination("profile", "Profile", Icons.Default.Person, "Profile Screen")

    companion object {
        val entries = listOf(Home, Expense, Profile)
    }
}
