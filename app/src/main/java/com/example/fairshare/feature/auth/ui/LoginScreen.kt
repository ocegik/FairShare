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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.*


@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // State to control UI visibility
    var showSignInButton by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val webClientId = context.getString(R.string.default_web_client_id)

    // --- LOGIC SECTION (Unchanged logic, just cleaner flow) ---

    // Try silent auto-login
    LaunchedEffect(Unit) {
        isLoading = true
        coroutineScope.launch {
            val helper = GoogleSignInHelper(context)
            helper.signInAuthorized(webClientId)
                .onSuccess { idToken ->
                    authViewModel.signInWithGoogle(idToken)
                }
                .onFailure { e ->
                    isLoading = false
                    if (e is NoCredentialException) {
                        showSignInButton = true
                    } else {
                        // Silent failure is okay here, just show button
                        showSignInButton = true
                        Log.e("LoginScreen", "Silent login failed: ${e.message}")
                    }
                }
        }
    }

    // Observe auth state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Loading -> {
                isLoading = true
            }
            is AuthState.Success -> {
                isLoading = false
                userViewModel.initializeUser()
                Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is AuthState.Error -> {
                isLoading = false
                showSignInButton = true
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    // --- UI SECTION ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 1. Logo / Branding
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.PieChart, // Or your App Logo
                        contentDescription = "Logo",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 2. Welcome Text
            Text(
                text = "FairShare",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Split bills, not friendships.\nSign in to start tracking.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. Loading Indicator OR Sign In Button
            // 3. Loading Indicator OR Sign In Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(60.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                } else {
                    // FORCE the top-level function usage by adding the package name
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showSignInButton,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        GoogleSignInButton(
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    val helper = GoogleSignInHelper(context)
                                    helper.signInFallback(webClientId)
                                        .onSuccess { idToken ->
                                            authViewModel.signInWithGoogle(idToken)
                                        }
                                        .onFailure { exception ->
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Sign-in failed: ${exception.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        )
                    }
                }
            }
        }

        // 4. Footer (Optional Terms text)
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        // Ensure you have the Google Icon drawable
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Logo",
            tint = Color.Unspecified, // Keep original Google colors
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Continue with Google",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
