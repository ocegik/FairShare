package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fairshare.navigation.Destination
import com.example.fairshare.viewmodel.AuthViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val destinations = listOf(Destination.Home, Destination.Expense, Destination.Profile)

    Scaffold(
        bottomBar = {
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
            if (destinations.any { it.route == currentDestination }) {
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.contentDescription) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    viewModel = authViewModel,
                    onSignOut = {
                        // Call authViewModel.signOut(context) or navigate to login
                    }
                )
            }
            composable(Destination.Expense.route) { ExpenseScreen(navController) }
            composable(Destination.Profile.route) { ProfileScreen(navController) }
        }
    }
}

