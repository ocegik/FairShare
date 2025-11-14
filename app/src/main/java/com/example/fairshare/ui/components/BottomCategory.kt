package com.example.fairshare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fairshare.core.ui.getCategoryIcon

@Composable
fun CategoryBottomSheet(
    entryType: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = if (entryType == "Income") {
        listOf("Salary", "Business", "Investment", "Freelance", "Gift",
            "Rental Income", "Bonus", "Other Income")
    } else {
        listOf("Food & Dining", "Transport", "Shopping", "Entertainment",
            "Bills & Utilities", "Health & Fitness", "Education", "Travel",
            "Groceries", "Other Expense")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        Text(
            text = "Select Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(category) }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = category,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
