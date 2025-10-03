package com.example.fairshare

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.fairshare.ui.screens.AppNavHost
import com.example.fairshare.ui.theme.FairShareTheme
import com.example.fairshare.viewmodel.AuthViewModel

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    FairShareTheme {
        AppNavHost(navController = navController, authViewModel = viewModel)
    }
}