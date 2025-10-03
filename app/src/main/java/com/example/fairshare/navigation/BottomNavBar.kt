package com.example.fairshare.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navController: NavHostController,
    destinations: List<Destination>
) {
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
