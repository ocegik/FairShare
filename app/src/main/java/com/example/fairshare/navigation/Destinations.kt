package com.example.fairshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavDestination(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
)

val bottomNavDestinations = listOf(
    BottomNavDestination(Screen.Home, "Home", Icons.Default.Home, "Home Screen"),
    BottomNavDestination(Screen.Expense, "Expense", Icons.Default.Add, "Expense Screen"),
    BottomNavDestination(Screen.Stats, "Stats", Icons.Default.Star, "Stats Screen")
)
