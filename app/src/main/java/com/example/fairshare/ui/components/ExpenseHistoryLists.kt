package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpenseHistoryList(type: String) {
    val placeholderExpenses = when (type) {
        "Personal" -> List(8) { index ->
            ExpenseItemData(
                title = "Personal Expense #$index",
                amount = (150..800).random(),
                date = "Oct ${8 + index}, 2025",
                category = listOf("Food", "Bills", "Subscriptions").random()
            )
        }

        "Group" -> List(6) { index ->
            ExpenseItemData(
                title = "Group Trip #$index",
                amount = (300..2500).random(),
                date = "Oct ${5 + index}, 2025",
                category = listOf("Trip", "Shared Rent", "Party", "Event").random()
            )
        }

        "Yours" -> List(10) { index ->
            ExpenseItemData(
                title = "Expense by You #$index",
                amount = (200..1500).random(),
                date = "Oct ${10 + index}, 2025",
                category = listOf("Personal", "Group", "Misc").random()
            )
        }

        else -> emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(placeholderExpenses) { expense ->
            ExpenseHistoryItem(expense)
        }
    }
}
data class ExpenseItemData(
    val title: String,
    val amount: Int,
    val date: String,
    val category: String
)

@Composable
fun ExpenseHistoryItem(expense: ExpenseItemData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(expense.category, fontSize = 13.sp, color = Color.Gray)
                Text(expense.date, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                "₹${expense.amount}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
