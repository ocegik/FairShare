package com.example.fairshare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fairshare.core.data.models.Group
import com.example.fairshare.core.data.models.GroupMember
import com.example.fairshare.viewmodel.GroupViewModel
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import com.example.fairshare.core.utils.formatDateTime


@Composable
fun GroupItem(
    group: Group,
    groupViewModel: GroupViewModel,
    isBookmarked: Boolean = false,
    onBookmarkClick: () -> Unit = {},
    onClick: () -> Unit
) {
    var preview by remember { mutableStateOf<List<GroupMember>>(emptyList()) }

    LaunchedEffect(group.groupId) {
        preview = groupViewModel.getGroupPreviewMembers(group)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBookmarked)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // --- Header Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        group.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "#${group.groupId.take(5)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = if (isBookmarked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // --- Member Section ---
            Text(
                text = "Members (${group.members.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                preview.take(5).forEach { member ->
                    AsyncImage(
                        model = member.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.width(8.dp))
                }

                if (group.members.size > preview.size) {
                    Text(
                        "+${group.members.size - preview.size} more",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // --- Footer Info ---
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                Text(
                    text = "Owner: ${preview.firstOrNull { it.isOwner }?.displayName ?: "Unknown"}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    text = "Created: ${formatDateTime(group.createdAt)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
