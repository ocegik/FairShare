package com.example.fairshare.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Expense : Screen("expense")
    object Stats : Screen("stats")
}