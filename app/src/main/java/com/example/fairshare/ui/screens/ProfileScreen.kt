package com.example.fairshare.ui.screens

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fairshare.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import com.example.fairshare.ui.components.BackButton
import com.example.fairshare.viewmodel.UserViewModel


@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit,
    userViewModel: UserViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val displayName by userViewModel.displayName.collectAsState()
    val photoUrl by userViewModel.photoUrl.collectAsState()
    val email by userViewModel.email.collectAsState()

    LaunchedEffect(displayName, email, photoUrl) {
        Log.d("ProfileScreen", "DisplayName=$displayName, Email=$email, Photo=$photoUrl")
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
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Text(
                text = "Hello, ${displayName}!",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Email: $email",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = { authViewModel.signOut(context, userViewModel) }) {
                Text("Sign Out")
            }
        }
    }
}
