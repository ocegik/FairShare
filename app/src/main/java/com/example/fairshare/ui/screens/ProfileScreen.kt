package com.example.fairshare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import com.example.fairshare.ui.components.BackButton


@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val userName by authViewModel.userName.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()
    val userPhotoUrl by authViewModel.userPhotoUrl.collectAsState()

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
                text = "Profile",
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
            if (userPhotoUrl != null) {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray, CircleShape)
                )
            }

            Text(
                text = "Hello, ${userName ?: "Guest"}!",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Email: ${userEmail ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = { authViewModel.signOut(context) }) {
                Text("Sign Out")
            }
        }
    }
}
