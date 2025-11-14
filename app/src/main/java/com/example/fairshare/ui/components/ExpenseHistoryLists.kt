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
import com.example.fairshare.core.data.models.ExpenseData
import com.example.fairshare.core.utils.formatDateTime

@Composable
fun ExpenseHistoryList(expenses: List<ExpenseData>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(expenses) { expense ->
            ExpenseHistoryItem(
                ExpenseItemData(
                    title = expense.title,
                    amount = expense.amount,
                    date = formatDateTime(expense.dateTime),
                    category = expense.category
                )
            )
        }
    }
}

data class ExpenseItemData(
    val title: String,
    val amount: Double,
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
