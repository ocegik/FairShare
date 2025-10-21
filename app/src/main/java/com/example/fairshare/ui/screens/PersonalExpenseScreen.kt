package com.example.fairshare.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.fairshare.ui.components.ExpenseFormScreen

@Composable
fun PersonalExpenseScreen(navController: NavHostController) {
    ExpenseFormScreen(navController, isGroupExpense = false)
}


