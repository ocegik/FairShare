package com.example.fairshare.ui.screens

import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fairshare.ui.graphs.DailyExpense
import com.example.fairshare.ui.graphs.MonthlyComparisonChart
import com.example.fairshare.ui.graphs.MonthlyExpenseData
import com.example.fairshare.ui.graphs.PieChart
import com.example.fairshare.ui.graphs.PieChartData

@Composable
fun StatsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // allows scrolling if needed
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Stats Overview",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        val data = listOf(
            PieChartData(30f, Color(0xFF2196F3)),
            PieChartData(20f, Color(0xFF4CAF50)),
            PieChartData(50f, Color(0xFF4CAF50))
        )

        PieChart(data = data)

        Spacer(modifier = Modifier.height(32.dp))

        val july = (1..30).map { DailyExpense(it, (50..300).random().toFloat()) }
        val august = (1..30).map { DailyExpense(it, (80..350).random().toFloat()) }
        val september = (1..30).map { DailyExpense(it, (100..400).random().toFloat()) }

        val months = listOf(
            MonthlyExpenseData("July", Color(0xFF2196F3), july),
            MonthlyExpenseData("August", Color(0xFF4CAF50), august),
            MonthlyExpenseData("September", Color(0xFFFF9800), september)
        )

        MonthlyComparisonChart(monthsData = months)

    }

}