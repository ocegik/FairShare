package com.example.fairshare.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavDestination(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
)

val bottomNavDestinations = listOf(
    BottomNavDestination(Screen.Home, "Home", Icons.Default.Home, "Home Screen"),
    BottomNavDestination(Screen.Group, "Group", Icons.Default.Groups, "Group Screen"),
    BottomNavDestination(Screen.Balances, "Balances", Icons.Default.AccountBalanceWallet, "Balance Screen"),
    BottomNavDestination(Screen.History, "History", Icons.Default.History, "History Screen")
)
