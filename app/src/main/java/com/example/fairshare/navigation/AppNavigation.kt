package com.example.fairshare.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.fairshare.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fairshare.data.models.AuthState
import com.example.fairshare.ui.screens.LoginScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fairshare.ui.screens.BalancesScreen
import com.example.fairshare.ui.screens.CreateGroupScreen
import com.example.fairshare.ui.screens.GroupDetailsScreen
import com.example.fairshare.ui.screens.GroupExpenseScreen
import com.example.fairshare.ui.screens.GroupScreen
import com.example.fairshare.ui.screens.HistoryScreen
import com.example.fairshare.ui.screens.PersonalExpenseScreen
import com.example.fairshare.ui.screens.HomeScreen
import com.example.fairshare.ui.screens.JoinGroupScreen
import com.example.fairshare.ui.screens.ProfileScreen
import com.example.fairshare.ui.screens.StatsScreen
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.HistoryViewModel
import com.example.fairshare.viewmodel.UserViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
    groupViewModel: GroupViewModel,
    historyViewModel: HistoryViewModel,
    debtViewModel: DebtViewModel
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {

                if (navController.currentDestination?.route == Screen.Login.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
            is AuthState.Error -> {

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
            modifier = Modifier.padding(paddingValues),

            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) },
            popEnterTransition = { fadeIn(animationSpec = tween(0)) },
            popExitTransition = { fadeOut(animationSpec = tween(0)) }
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel,
                    userViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(navController, userViewModel)
            }

            composable(Screen.PersonalExpense.route) {
                PersonalExpenseScreen(navController, expenseViewModel, authViewModel, userViewModel, debtViewModel)
            }

            composable(Screen.GroupExpense.route) {
                GroupExpenseScreen(navController, expenseViewModel, authViewModel, userViewModel, groupViewModel, debtViewModel)
            }

            composable(Screen.History.route) {
                HistoryScreen(navController, historyViewModel)
            }

            composable(Screen.Group.route) {
                GroupScreen(navController, groupViewModel, authViewModel, userViewModel)
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
                    userViewModel,
                    navController
                )
            }

            composable(Screen.CreateGroup.route) {
                CreateGroupScreen(navController, groupViewModel, authViewModel)
            }

            composable(Screen.JoinGroup.route) {
                JoinGroupScreen(navController)
            }
            composable(
                route = "group_details/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")!!
                GroupDetailsScreen(groupId, groupViewModel, userViewModel)
            }
            composable(Screen.Balances.route) {
                BalancesScreen(navController, debtViewModel, authViewModel,  groupViewModel)
            }

        }
    }
}