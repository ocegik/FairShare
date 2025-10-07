package com.example.fairshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
    BottomNavDestination(Screen.Stats, "Stats", Icons.Default.Star, "Stats Screen"),
    BottomNavDestination(Screen.Profile, "Profile", Icons.Default.AccountCircle, "Profile Screen")
)
