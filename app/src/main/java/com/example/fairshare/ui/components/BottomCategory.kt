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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fairshare.core.ui.getCategoryIcon
import com.example.fairshare.R

@Composable
fun CategoryBottomSheet(
    entryType: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = if (entryType == "Income") {
        listOf(
            R.string.inc_salary,
            R.string.inc_business,
            R.string.inc_investment,
            R.string.inc_freelance,
            R.string.inc_gift,
            R.string.inc_rental_income,
            R.string.inc_bonus,
            R.string.inc_other_income
        )
    } else {
        listOf(
            R.string.exp_food_dining,
            R.string.exp_transport,
            R.string.exp_shopping,
            R.string.exp_entertainment,
            R.string.exp_bills_utilities,
            R.string.exp_health_fitness,
            R.string.exp_education,
            R.string.exp_travel,
            R.string.exp_groceries,
            R.string.exp_other_expense
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        Text(
            text = stringResource(R.string.select_category),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        categories.forEach { categoryRes ->
            val label = stringResource(categoryRes)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(label) }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getCategoryIcon(label),
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
