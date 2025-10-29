package com.example.fairshare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.UserPreferences
import com.example.fairshare.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val appContext: Application
) : ViewModel() {

    private val userPrefs = UserPreferences(appContext)

    private val _userProfile = MutableStateFlow<Map<String, Any>?>(null)
    val displayName = _userProfile.map { it?.get("displayName") as? String ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val email = _userProfile.map { it?.get("email") as? String ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val photoUrl = _userProfile.map { it?.get("photoUrl") as? String ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserFromCache()
        refreshUserFromFirestore()
    }
    private fun loadUserFromCache() {
        viewModelScope.launch {
            userPrefs.userData.firstOrNull()?.let { localData ->
                if (localData["displayName"].isNullOrEmpty()) return@let
                _userProfile.value = localData
            }

            // Keep collecting for changes (optional)
            userPrefs.userData.collect { localData ->
                if (localData["displayName"].isNullOrEmpty()) return@collect
                _userProfile.value = localData
            }
        }
    }

    fun refreshUserFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        userRepository.getUser(uid) { data ->
            if (data != null) {
                _userProfile.value = data
                viewModelScope.launch {
                    userPrefs.saveUser(
                        displayName = data["displayName"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        photoUrl = data["photoUrl"] as? String ?: ""
                    )
                }
            }
        }
    }

    fun clearUser() {
        viewModelScope.launch { userPrefs.clearUser() }
        _userProfile.value = emptyMap()
    }


    // Load current user's profile from Firestore
    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            _isLoading.value = true

            userRepository.getUser(uid) { data ->
                _userProfile.value = data
                _isLoading.value = false
            }
        } else {
            Log.e("UserViewModel", "❌ Cannot load user - UID is NULL!")
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
