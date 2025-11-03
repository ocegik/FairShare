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
import com.example.fairshare.data.models.Group
import com.example.fairshare.ui.components.BackButton
import com.example.fairshare.ui.components.PasswordField
import com.example.fairshare.ui.components.TitleField
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.GroupViewModel

@Composable
fun CreateGroupScreen(navController: NavController, groupViewModel: GroupViewModel, authViewModel: AuthViewModel) {

    val userId by authViewModel.currentUserId.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isCreating by remember { mutableStateOf(false) }

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
                text = "Create Group",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Profile info section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleField(title) { title = it }
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

            Button(
                onClick = {
                    Log.d("ExpenseForm", "=== SUBMIT BUTTON CLICKED ===")

                    val currentUserId = userId
                    if (currentUserId.isNullOrBlank()) {
                        Toast.makeText(
                            context,
                            "User not authenticated",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val isValid = title.isNotBlank() && password.isNotBlank()


                    if (isValid) {
                        isCreating = true

                        val groupId = groupViewModel.generateGroupId()

                        val newGroup = Group(
                            groupId = groupId,
                            name = title,
                            owner = currentUserId,
                            password = password,
                            members = listOf(currentUserId), // Owner is first member
                            createdAt = System.currentTimeMillis()
                        )
                        groupViewModel.addGroup(newGroup)

                        Log.d("CreateGroup", "Group created: $groupId")

                        // Show success message
                        Toast.makeText(
                            context,
                            "Group created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        //groupViewModel
                        navController.popBackStack()
                    } else {
                        Log.e("GroupForm", "Validation failed - group not created")

                        when {
                            title.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    "Please enter a group name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            password.length < 6 -> {
                                showError = true
                                Toast.makeText(
                                    context,
                                    "Password must be at least 6 characters",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                enabled = !isCreating && title.isNotBlank() && password.length >= 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Group", fontSize = 16.sp)
                }
            }
        }
    }
}