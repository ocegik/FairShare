package com.example.fairshare

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.fairshare.navigation.AppNavigation
import com.example.fairshare.ui.theme.FairShareTheme
import com.example.fairshare.viewmodel.AuthViewModel

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    FairShareTheme {
        AppNavigation(navController = navController, authViewModel = authViewModel)
    }
}
