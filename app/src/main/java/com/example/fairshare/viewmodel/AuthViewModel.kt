package com.example.fairshare.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.credentials.ClearCredentialStateRequest
import com.example.fairshare.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    companion object{
        private const val TAG = "AuthViewModel"
    }

    init {
        Log.d(TAG, "=== AuthViewModel CREATED ===")
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = repository.getCurrentUser()
        Log.d(TAG, "checkCurrentUser: user = $user")
        user?.let {
            Log.d(TAG, "User found - uid: ${it.uid}, name: ${it.displayName}")
            _authState.value = AuthState.Success(it)
        } ?: Log.d(TAG, "No current user")
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            repository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    Log.d(TAG, "✅ Sign-in SUCCESS - uid: ${user.uid}, name: ${user.displayName}")
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { exception ->
                    Log.e(TAG, "❌ Sign-in FAILED: ${exception.message}")
                    _authState.value = AuthState.Error(
                        exception.message ?: "Sign in failed"
                    )
                }
        }
    }

    fun signOut(context: Context, userViewModel: UserViewModel? = null) {
        viewModelScope.launch {
            try {
                repository.signOut()
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())

                // Wait until user cache is fully cleared
                userViewModel?.clearUser()
                delay(500) // 🔥 gives datastore & flows time to reset

                _authState.value = AuthState.Idle
                Log.d(TAG, "✅ User fully signed out and cache cleared")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during sign out", e)
                _authState.value = AuthState.Idle
            }
        }
    }


    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // Only expose whether user is authenticated, not their profile data
    val isAuthenticated: StateFlow<Boolean> = _authState
        .map { state ->
            val isAuth = state is AuthState.Success
            Log.d("AuthViewModel", "isAuthenticated: $isAuth")
            isAuth
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val currentUserId: StateFlow<String?> = _authState
        .map { state ->
            when (state) {
                is AuthState.Success -> {
                    Log.d(TAG, "currentUserId: ${state.user.uid}")
                    state.user.uid
                }
                else -> {
                    Log.d(TAG, "currentUserId: null (state: ${state::class.simpleName})")
                    null
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
