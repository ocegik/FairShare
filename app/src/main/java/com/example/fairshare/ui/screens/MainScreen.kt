package com.example.fairshare.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fairshare.navigation.Destination
import com.example.fairshare.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier
    ) {
        composable(Destination.Home.route) {
            HomeScreen(
                viewModel = authViewModel,
                onSignOut = {
                    // handle sign out or navigate to login
                }
            )
        }
        composable(Destination.Expense.route) { ExpenseScreen(navController) }
        composable(Destination.Stats.route) { ProfileScreen(navController) }
    }
}


