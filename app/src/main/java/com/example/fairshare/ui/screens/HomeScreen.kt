package com.example.fairshare.ui.screens

import androidx.compose.runtime.Composable
import com.example.fairshare.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fairshare.data.models.AuthState

@Composable
fun HomeScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Idle) {
            onSignOut()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = authState) {
                is AuthState.Success -> {
                    Text(
                        text = "Hello, ${state.user.displayName}!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = state.user.email ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { viewModel.signOut(context) }) {
                        Text("Sign Out")
                    }
                }
                else -> {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
