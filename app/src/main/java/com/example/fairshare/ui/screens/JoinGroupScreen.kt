package com.example.fairshare.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fairshare.ui.components.BackButton
import com.example.fairshare.ui.components.PasswordField
import com.example.fairshare.ui.components.TitleField
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.GroupViewModel

@Composable
fun JoinGroupScreen(navController: NavController,
                    authViewModel: AuthViewModel,
                    groupViewModel: GroupViewModel
) {

    val userId by authViewModel.currentUserId.collectAsState()
    val uiState by groupViewModel.uiState.collectAsState()

    var groupId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is GroupViewModel.GroupUiState.Success -> {
                isJoining = false
                Toast.makeText(context, "Joined Successfully!", Toast.LENGTH_SHORT).show()
                groupViewModel.clearUiState()
                navController.popBackStack()
            }

            is GroupViewModel.GroupUiState.Error -> {
                isJoining = false
                Toast.makeText(context, (uiState as GroupViewModel.GroupUiState.Error).message, Toast.LENGTH_SHORT).show()
                groupViewModel.clearUiState()
            }

            GroupViewModel.GroupUiState.Loading -> isJoining = true
            GroupViewModel.GroupUiState.Idle -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp) // more balanced padding
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            BackButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "Join Group",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TitleField(groupId) { groupId = it }
            Spacer(modifier = Modifier.height(24.dp))

            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    showError = it.length < 6
                },
                isError = showError,
                errorMessage = if (showError) "Password must be at least 6 characters" else null
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val currentUserId = userId
                    if (currentUserId.isNullOrBlank()) {
                        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (groupId.isBlank()) {
                        Toast.makeText(context, "Enter group ID", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password.length < 6) {
                        showError = true
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isJoining = true
                    groupViewModel.joinGroup(groupId, password, currentUserId)
                },
                enabled = !isJoining && groupId.isNotBlank() && password.length >= 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isJoining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Join Group", fontSize = 16.sp)
                }
            }
        }
    }
}