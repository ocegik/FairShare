package com.example.fairshare.navigation

sealed class Screen(val route: String) {
    object Login : Screen("Login")
    object Home : Screen("Home")

}