package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fairshare.ui.components.SubHeader
import com.example.fairshare.viewmodel.AuthViewModel
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun AccountSettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val email by userViewModel.email.collectAsState()

    Scaffold(
        topBar = {
            SubHeader(title = "About", onBack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingsSectionTitle("User Details")
            Text("Logged in as:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(email, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(32.dp))

            SettingsSectionTitle("Session")

            Button(
                onClick = {
                    authViewModel.signOut(context, userViewModel)
                    // Depending on your nav logic, you might need to navigate to Login here
                    // navController.navigate("login") { popUpTo(0) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}