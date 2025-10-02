package com.example.fairshare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun NavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.Home.route){
        composable(Screen.Login.route) {"Login"}
        composable(Screen.Home.route) {"Home"}
    }
}