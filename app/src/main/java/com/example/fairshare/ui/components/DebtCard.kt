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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fairshare.R
import com.example.fairshare.core.data.models.DebtData

@Composable
fun DebtCard(
    debt: DebtData,
    fromName: String,
    toName: String,
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

                val title = if (debt.fromUserId == currentUserId)
                    "${stringResource(R.string.you_owe_label)} $toName"
                else
                    "$fromName ${stringResource(R.string.owed_to_you)}"

                Column {
                    Text(
                        text = title,
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
                        Text(stringResource(R.string.settle))
                    }
                } else if (debt.status == "settled") {
                    Text(
                        text = stringResource(R.string.settled),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
