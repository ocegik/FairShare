package com.example.fairshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fairshare.data.models.AuthState
import com.example.fairshare.ui.screens.HomeScreen
import com.example.fairshare.ui.screens.LoginScreen
import com.example.fairshare.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val viewModel: AuthViewModel = viewModel()
                val authState by viewModel.authState.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (authState) {
                        is AuthState.Success -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onSignOut = { /* Navigate to login */ }
                            )
                        }
                        else -> {
                            LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = { /* Already handled */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

