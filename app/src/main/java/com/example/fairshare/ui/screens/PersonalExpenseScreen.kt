package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.core.ui.BackButton
import com.example.fairshare.ui.components.ExpenseFormScreen
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.DebtViewModel
import com.example.fairshare.viewmodel.ExpenseViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun PersonalExpenseScreen(
    navController: NavHostController,
    expenseViewModel: ExpenseViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    debtViewModel: DebtViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = { navController.popBackStack() })
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Personal Expense",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ExpenseFormScreen(
                    navController,
                    isGroupExpense = false,
                    expenseViewModel,
                    authViewModel,
                    userViewModel,
                    debtViewModel
                )
            }
        }
    }
}



