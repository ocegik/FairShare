package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fairshare.navigation.Screen
import com.example.fairshare.ui.components.GroupItem
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.GroupViewModel

@Composable
fun GroupScreen(navController: NavController,
                groupViewModel: GroupViewModel,
                authViewModel: AuthViewModel) {

    val userId by authViewModel.currentUserId.collectAsState()

    LaunchedEffect(userId) {
        if (!userId.isNullOrBlank()) {
            groupViewModel.loadGroupsForUser(userId!!)
        }
    }

    val userGroups by groupViewModel.userGroups.collectAsState()
    val uiState by groupViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // allows scrolling if needed
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Room Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Need a new group?", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { navController.navigate(Screen.CreateGroup.route) }) {
                Text("Create", color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.width(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Join a group?", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { navController.navigate(Screen.JoinGroup.route) }) {
                Text("Create", color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Loading UI
        if (uiState is GroupViewModel.GroupUiState.Loading) {
            CircularProgressIndicator()
            return@Column
        }

        // SHOW GROUPS HERE
        if (userGroups.isEmpty()) {
            Text("You are not in any group yet.")
            return@Column
        }

        Text("Your Groups", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(12.dp))

        userGroups.forEach { group ->
            GroupItem(group = group) {
                // navigate to group details
                navController.navigate("group_details/${group.groupId}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}