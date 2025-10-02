package com.example.fairshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fairshare.core.GoogleSignInHelper
import com.example.fairshare.data.models.AuthState
import com.example.fairshare.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.fairshare.R

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showSignInButton by remember { mutableStateOf(false) }

    val webClientId = context.getString(R.string.default_web_client_id)

    // Try silent auto-login for returning users
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val helper = GoogleSignInHelper(context)
            helper.signInAuthorized(webClientId)
                .onSuccess { idToken ->
                    viewModel.signInWithGoogle(idToken)
                }
                .onFailure { e ->
                    if (e is NoCredentialException) {
                        // No saved credentials → show button for new users
                        showSignInButton = true
                    } else {
                        Toast.makeText(
                            context,
                            "Sign-in failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    // Observe auth state changes
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    "Welcome ${state.user.displayName}!",
                    Toast.LENGTH_SHORT
                ).show()
                onLoginSuccess()
            }
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    "Error: ${state.message}",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetState()
            }
            else -> {}
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
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.headlineMedium
            )

            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            }

            // Show Google Sign-In button only if fallback is needed
            if (showSignInButton) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val helper = GoogleSignInHelper(context)
                            helper.signInFallback(webClientId)
                                .onSuccess { idToken ->
                                    viewModel.signInWithGoogle(idToken)
                                }
                                .onFailure { exception ->
                                    Toast.makeText(
                                        context,
                                        "Sign-in failed: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                ) {
                    Text("Sign in with Google")
                }
            }
        }
    }
}
