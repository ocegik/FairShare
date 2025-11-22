package com.example.fairshare.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object PersonalExpense : Screen("expense")
    object GroupExpense : Screen("groupExpense")
    object Stats : Screen("stats")
    object Profile : Screen("profile")
    object History : Screen("history")
    object Group : Screen("group")
    object CreateGroup : Screen("createGroup")
    object JoinGroup : Screen("joinGroup")
    object GroupDetails: Screen("group_details/{groupId}")
    object Balances: Screen("balances")
    object CreateDebt: Screen("createDebt")
    object Onboarding: Screen("onboarding")
    object ProfileSetup: Screen("profileSetup")
}
