package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.fairshare.navigation.Screen


@Composable
fun LargeFloatingActionButtonSample(navController: NavHostController) {
    LargeFloatingActionButton(onClick = { navController.navigate(Screen.Expense.route)  }) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Localized description",
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
        )
    }
}
