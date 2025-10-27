package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Map<String, Any>?>(null)
    val userProfile: StateFlow<Map<String, Any>?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Derived state for UI
    val displayName: StateFlow<String?> = _userProfile
        .map { it?.get("displayName") as? String }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val email: StateFlow<String?> = _userProfile
        .map { it?.get("email") as? String }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val photoUrl: StateFlow<String?> = _userProfile
        .map { it?.get("photoUrl") as? String }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Load current user's profile from Firestore
    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            _isLoading.value = true
            userRepository.getUser(uid) { data ->
                _userProfile.value = data
                _isLoading.value = false
            }
        }
    }

    // Update user profile (bio, displayName, etc.)
    fun updateProfile(updates: Map<String, Any>) {
        val uid = auth.currentUser?.uid ?: return

        _isLoading.value = true
        userRepository.updateUser(uid, updates) { success ->
            _isLoading.value = false
            if (success) {
                // Reload to get updated data
                loadCurrentUser()
            }
        }
    }

    // Update specific fields
    fun updateDisplayName(newName: String) {
        updateProfile(mapOf("displayName" to newName))
    }

    fun updateBio(newBio: String) {
        updateProfile(mapOf("bio" to newBio))
    }

    fun updatePhotoUrl(newPhotoUrl: String) {
        updateProfile(mapOf("photoUrl" to newPhotoUrl))
    }

    // Delete user account (removes Firestore data)
    fun deleteUserAccount() {
        val uid = auth.currentUser?.uid ?: return

        userRepository.deleteUser(uid) { success ->
            if (success) {
                _userProfile.value = null
            }
        }
    }
}
