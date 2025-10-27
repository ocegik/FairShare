package com.example.fairshare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryBottomSheet(
    entryType: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = if (entryType == "Income") {
        listOf(
            "Salary",
            "Business",
            "Investment",
            "Freelance",
            "Gift",
            "Rental Income",
            "Bonus",
            "Other Income"
        )
    } else {
        listOf(
            "Food & Dining",
            "Transport",
            "Shopping",
            "Entertainment",
            "Bills & Utilities",
            "Health & Fitness",
            "Education",
            "Travel",
            "Groceries",
            "Other Expense"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Icon(
            imageVector = if (entryType == "Income")
                Icons.Default.ArrowUpward
            else
                Icons.Default.ArrowDownward,
            contentDescription = entryType,
            tint = if (entryType == "Income")
                Color(0xFF4CAF50)
            else
                Color(0xFFF44336)
        )

        categories.forEach { category ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(category) },
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Optional: Add category icons
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = category,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
