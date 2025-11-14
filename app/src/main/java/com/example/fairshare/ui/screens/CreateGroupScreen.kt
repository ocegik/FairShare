package com.example.fairshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fairshare.core.data.models.Group
import com.example.fairshare.core.ui.BackButton
import com.example.fairshare.core.ui.PasswordField
import com.example.fairshare.core.ui.TitleField
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
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {

        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = { navController.popBackStack() })
            Spacer(Modifier.width(12.dp))
            Text(
                "Create Group",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(30.dp))

        // --- FORM CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                TitleField(title) { title = it }

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
                        val uid = userId
                        if (uid.isNullOrBlank()) {
                            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (title.isBlank()) {
                            Toast.makeText(context, "Please enter a group name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (password.length < 6) {
                            showError = true
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isCreating = true

                        val newGroup = Group(
                            groupId = groupViewModel.generateGroupId(),
                            name = title,
                            owner = uid,
                            password = password,
                            members = listOf(uid),
                            createdAt = System.currentTimeMillis()
                        )

                        groupViewModel.addGroup(newGroup)
                        Toast.makeText(context, "Group created successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    enabled = !isCreating && title.isNotBlank() && password.length >= 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Group", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}