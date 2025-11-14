package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.ui.components.BackButton
import com.example.fairshare.ui.components.ExpenseFormScreen
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun PersonalExpenseScreen(navController: NavHostController,
                          expenseViewModel: ExpenseViewModel,
                          authViewModel: AuthViewModel,
                          userViewModel: UserViewModel,
                          debtViewModel: DebtViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp) // more balanced padding
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            BackButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "Personal Expense",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        ExpenseFormScreen(navController, isGroupExpense = false, expenseViewModel, authViewModel, userViewModel, debtViewModel)
    }
}


