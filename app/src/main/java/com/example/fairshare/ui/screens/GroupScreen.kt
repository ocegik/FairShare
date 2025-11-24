package com.example.fairshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fairshare.ui.components.CreateJoinSection
import com.example.fairshare.ui.components.GroupItem
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.GroupViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun GroupScreen(
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val userId by authViewModel.currentUserId.collectAsState()
    val bookmarkedGroupId by userViewModel.bookmarkedGroupId.collectAsState()
    val userGroups by groupViewModel.userGroups.collectAsState()
    val uiState by groupViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        if (!userId.isNullOrBlank()) {
            groupViewModel.loadGroupsForUser(userId!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {

        // Header ---------------------------------------------------------------
        Text(
            "Groups",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(20.dp))

        // Loading --------------------------------------------------------------
        if (uiState is GroupViewModel.GroupUiState.Loading) {
            CircularProgressIndicator()
            return@Column
        }

        // Empty State ----------------------------------------------------------
        if (userGroups.isEmpty()) {
            Text(
                text = "You are not in any group yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(30.dp))

            // Bottom CTA for empty case
            CreateJoinSection(navController)
            return@Column
        }

        userGroups.forEach { group ->
            GroupItem(
                group = group,
                groupViewModel = groupViewModel,
                isBookmarked = group.groupId == bookmarkedGroupId,
                onBookmarkClick = {
                    val newId = if (group.groupId == bookmarkedGroupId) null else group.groupId
                    userViewModel.updateBookMarkedGroup(newId ?: "") { success ->
                        if (success) {
                            Toast.makeText(
                                context,
                                if (newId != null) "Bookmarked ${group.name}"
                                else "Removed bookmark",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onClick = {
                    navController.navigate("group_details/${group.groupId}")
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(40.dp))

        // Bottom Create + Join -------------------------------------------------
        CreateJoinSection(navController)
    }
}

