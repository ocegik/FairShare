package com.example.fairshare

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.fairshare.navigation.AppNavigation
import com.example.fairshare.ui.theme.FairShareTheme
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.HistoryViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val groupViewModel: GroupViewModel = hiltViewModel()
    val expenseViewModel: ExpenseViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val debtViewModel: DebtViewModel = hiltViewModel()

    FairShareTheme {
        AppNavigation(navController, authViewModel, userViewModel, expenseViewModel, groupViewModel, historyViewModel, debtViewModel)
    }
}
