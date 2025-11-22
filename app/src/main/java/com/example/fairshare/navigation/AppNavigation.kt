package com.example.fairshare.navigation

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fairshare.core.data.models.AuthState
import com.example.fairshare.feature.auth.ui.LoginScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fairshare.ui.screens.BalancesScreen
import com.example.fairshare.ui.screens.CreateDebtScreen
import com.example.fairshare.ui.screens.CreateGroupScreen
import com.example.fairshare.ui.screens.GroupDetailsScreen
import com.example.fairshare.ui.screens.GroupExpenseScreen
import com.example.fairshare.ui.screens.GroupScreen
import com.example.fairshare.ui.screens.HistoryScreen
import com.example.fairshare.ui.screens.PersonalExpenseScreen
import com.example.fairshare.ui.screens.HomeScreen
import com.example.fairshare.ui.screens.JoinGroupScreen
import com.example.fairshare.ui.screens.OnboardingScreen
import com.example.fairshare.ui.screens.ProfileScreen
import com.example.fairshare.ui.screens.ProfileSetupScreen
import com.example.fairshare.ui.screens.StatsScreen
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.HistoryViewModel
import com.example.fairshare.viewmodel.UserViewModel
import androidx.core.content.edit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
    groupViewModel: GroupViewModel,
    historyViewModel: HistoryViewModel,
    debtViewModel: DebtViewModel,
    onboardingDone: Boolean,
    profileSetupDone: Boolean
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in bottomNavDestinations.map { it.screen.route }

    LaunchedEffect(authState) {
        if (authState !is AuthState.Success && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }


    val startDestination = when (authState) {
        is AuthState.Success -> {
            when {
                !onboardingDone -> Screen.Onboarding.route
                !profileSetupDone -> Screen.ProfileSetup.route
                else -> Screen.Home.route
            }
        }
        else -> Screen.Login.route
    }



    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController, bottomNavDestinations)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
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
                        val destination = when {
                            !onboardingDone -> Screen.Onboarding.route
                            !profileSetupDone -> Screen.ProfileSetup.route
                            else -> Screen.Home.route
                        }
                        navController.navigate(destination) {
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
                JoinGroupScreen(navController, authViewModel, groupViewModel)
            }
            composable(
                route = "group_details/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")!!
                GroupDetailsScreen(groupId, groupViewModel, userViewModel)
            }
            composable(Screen.Balances.route) {
                BalancesScreen(navController, debtViewModel, authViewModel,  groupViewModel, userViewModel)
            }

            composable(Screen.CreateDebt.route){
                CreateDebtScreen(navController, debtViewModel, userViewModel, authViewModel)
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        // Save to SharedPreferences
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit {
                                putBoolean("onboarding_done", true)
                            }

                        navController.navigate(Screen.ProfileSetup.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ProfileSetup.route) {
                ProfileSetupScreen(
                    onSetupComplete = { name, handle, imageUri ->
                        // Save to Firestore
                        // userViewModel.createUserProfile(name, handle, imageUri)

                        // Save to SharedPreferences
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit {
                                putBoolean("profile_setup_done", true)
                            }

                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                        }
                    }
                )
            }

        }
    }
}