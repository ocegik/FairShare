package com.example.fairshare.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.firebase.AuthRepository
import com.example.fairshare.data.models.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.credentials.ClearCredentialStateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        repository.getCurrentUser()?.let { user ->
            _authState.value = AuthState.Success(user)
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            repository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Sign in failed"
                    )
                }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            repository.signOut()
            val cm = CredentialManager.create(context)
            cm.clearCredentialState(ClearCredentialStateRequest())
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // Only expose whether user is authenticated, not their profile data
    val isAuthenticated: StateFlow<Boolean> = _authState
        .map { state -> state is AuthState.Success }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val currentUserId: StateFlow<String?> = _authState
        .map { state ->
            when (state) {
                is AuthState.Success -> state.user.uid
                else -> null
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
