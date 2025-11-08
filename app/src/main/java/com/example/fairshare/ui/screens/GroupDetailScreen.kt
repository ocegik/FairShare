package com.example.fairshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fairshare.data.models.Group
import com.example.fairshare.ui.components.formatDateTime
import com.example.fairshare.viewmodel.GroupViewModel

@Composable
fun GroupDetailsScreen(
    groupId: String,
    groupViewModel: GroupViewModel
) {
    val uiState by groupViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var group by remember { mutableStateOf<Group?>(null) }

    // Load the group data
    LaunchedEffect(groupId) {
        val result = groupViewModel.getGroup(groupId)
        result.onSuccess { fetchedGroup ->
            group = fetchedGroup
        }.onFailure {
            Toast.makeText(context, "Group not found", Toast.LENGTH_SHORT).show()
        }

    }

    if (group == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = group!!.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        Text("Group ID: ${group!!.groupId}")
        Text("Owner: ${group!!.owner}")
        Text("Members: ${group!!.members.size}")
        Text("Created: ${formatDateTime(group!!.createdAt)}")

        Spacer(Modifier.height(20.dp))

        Text("Members List", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        group!!.members.forEach { memberId ->
            Text(text = "• $memberId", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
