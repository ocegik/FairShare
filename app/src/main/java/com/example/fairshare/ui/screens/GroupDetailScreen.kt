package com.example.fairshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fairshare.core.data.models.GroupUiData
import com.example.fairshare.core.utils.formatDateTime
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun GroupDetailsScreen(
    groupId: String,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel
) {
    val currentUserId by authViewModel.currentUserId.collectAsState()
    val context = LocalContext.current
    var uiData by remember { mutableStateOf<GroupUiData?>(null) }
    val bookmarkedGroupId by userViewModel.bookmarkedGroupId.collectAsState()
    val isBookmarked = groupId == bookmarkedGroupId

    LaunchedEffect(groupId) {
        groupViewModel.getGroupFullDetails(groupId)
            .onSuccess { uiData = it }
            .onFailure { Toast.makeText(context, "Group not found", Toast.LENGTH_SHORT).show() }
    }

    if (uiData == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val group = uiData!!.group
    val members = uiData!!.members

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with bookmark
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                group.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    val newBookmarkId = if (isBookmarked) null else groupId
                    userViewModel.updateBookMarkedGroup(newBookmarkId ?: "") { success ->
                        if (success) {
                            val message = if (newBookmarkId != null)
                                "Bookmarked ${group.name}"
                            else
                                "Removed bookmark"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text("Created: ${formatDateTime(group.createdAt)}")
        Text("Members: ${members.size}")

        Spacer(Modifier.height(24.dp))

        Text("Members", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        members.forEach { member ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                AsyncImage(
                    model = member.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(member.displayName ?: "Unknown")
                    Text(
                        member.email ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                if (member.isOwner) {
                    Spacer(Modifier.weight(1f))
                    Text("Owner", color = MaterialTheme.colorScheme.primary)
                }
            }

        }
        Spacer(Modifier.height(24.dp))

        Text("Actions", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (group.owner == currentUserId) {
            OwnerActions(
                groupId = groupId,
                currentUserId = currentUserId ?: "",
                groupViewModel = groupViewModel
            )
        } else {
            MemberActions(
                groupId = groupId,
                currentUserId = currentUserId ?: "",
                groupViewModel = groupViewModel
            )
        }

    }
}
