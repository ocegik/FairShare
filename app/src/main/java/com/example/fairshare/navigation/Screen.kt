package com.example.fairshare.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object PersonalExpense : Screen("expense")
    object GroupExpense : Screen("groupExpense")
    object Stats : Screen("stats")
    object Profile : Screen("profile")
}