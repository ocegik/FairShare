package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fairshare.data.models.DebtData

@Composable
fun DebtCard(
    debt: DebtData,
    currentUserId: String,
    onSettle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (debt.status == "settled")
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (debt.fromUserId == currentUserId)
                            "You owe ${debt.toUserId}"
                        else "${debt.fromUserId} owes you",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "₹${String.format("%.2f", debt.amount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (debt.status == "pending" && debt.fromUserId == currentUserId) {
                    Button(
                        onClick = onSettle,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Settle")
                    }
                } else if (debt.status == "settled") {
                    Text(
                        text = "Settled",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}