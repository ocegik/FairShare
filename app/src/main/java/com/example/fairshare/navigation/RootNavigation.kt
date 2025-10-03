package com.example.fairshare.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.fairshare.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.fairshare.ui.screens.AppNavHost

@Composable
fun RootNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val destinations = listOf(Destination.Home, Destination.Expense, Destination.Stats)

    Scaffold(
        bottomBar = { BottomNavBar(navController, destinations) }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            authViewModel = authViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
