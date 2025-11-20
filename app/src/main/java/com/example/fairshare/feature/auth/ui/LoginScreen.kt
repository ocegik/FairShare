package com.example.fairshare.feature.auth.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fairshare.core.auth.GoogleSignInHelper
import com.example.fairshare.core.data.models.AuthState
import com.example.fairshare.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.fairshare.R
import com.example.fairshare.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showSignInButton by remember { mutableStateOf(true) }

    val webClientId = context.getString(R.string.default_web_client_id)

    // Try silent auto-login for returning users
    LaunchedEffect(Unit) {
        coroutineScope.launch {

            val helper = GoogleSignInHelper(context)
            helper.signInAuthorized(webClientId)
                .onSuccess { idToken ->
                    authViewModel.signInWithGoogle(idToken)
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
                Log.d("LoginScreen", "=== Auth SUCCESS ===")
                Log.d("LoginScreen", "User: ${state.user.uid}")
                Log.d("LoginScreen", "Calling loadCurrentUser()...")
                userViewModel.initializeUser()

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
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 36.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Welcome to FairShare",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Track and split expenses easily with your friends.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )

                if (authState is AuthState.Loading) {
                    CircularProgressIndicator()
                }

                if (showSignInButton) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val helper = GoogleSignInHelper(context)
                                helper.signInFallback(webClientId)
                                    .onSuccess { idToken ->
                                        authViewModel.signInWithGoogle(idToken)
                                    }
                                    .onFailure { exception ->
                                        Toast.makeText(
                                            context,
                                            "Sign-in failed: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Sign in with Google",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
