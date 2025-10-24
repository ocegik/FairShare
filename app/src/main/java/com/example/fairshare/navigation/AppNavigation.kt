package com.example.fairshare.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.fairshare.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fairshare.data.models.AuthState
import com.example.fairshare.ui.screens.LoginScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fairshare.ui.screens.GroupExpenseScreen
import com.example.fairshare.ui.screens.GroupScreen
import com.example.fairshare.ui.screens.HistoryScreen
import com.example.fairshare.ui.screens.PersonalExpenseScreen
import com.example.fairshare.ui.screens.HomeScreen
import com.example.fairshare.ui.screens.ProfileScreen
import com.example.fairshare.ui.screens.StatsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // User logged in - navigate to home if not already there
                if (navController.currentDestination?.route == Screen.Login.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
            is AuthState.Error -> {
                // Auth failed - ensure on login screen
                if (navController.currentDestination?.route != Screen.Login.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            AuthState.Idle, AuthState.Loading -> {
                // Don't navigate during these states
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in bottomNavDestinations.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController, bottomNavDestinations)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (authState is AuthState.Success) {
                Screen.Home.route
            } else {
                Screen.Login.route
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(navController, authViewModel)
            }

            composable(Screen.PersonalExpense.route) {
                PersonalExpenseScreen(navController)
            }
            composable(Screen.GroupExpense.route) {
                GroupExpenseScreen(navController)
            }
            composable(Screen.History.route) {
                HistoryScreen(navController)
            }
            composable(Screen.Group.route) {
                GroupScreen(navController)
            }

            composable(Screen.Stats.route) {
                StatsScreen(navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel,
                    onSignOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    navController
                )
            }
        }
    }
}