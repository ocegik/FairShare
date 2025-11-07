package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fairshare.ui.components.ExpenseHistoryList
import com.example.fairshare.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(navController: NavController,
                  historyViewModel: HistoryViewModel) {

    val personal by historyViewModel.personalExpenses.collectAsState()
    val group by historyViewModel.groupExpenses.collectAsState()
    val yours by historyViewModel.yourExpenses.collectAsState()
    val loading by historyViewModel.loading.collectAsState()

    val tabs = listOf("Personal", "Group", "Yours")
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Expense History",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        when (selectedTab) {
            0 -> ExpenseHistoryList(personal)
            1 -> ExpenseHistoryList(group)
            2 -> ExpenseHistoryList(yours)
        }
    }
}