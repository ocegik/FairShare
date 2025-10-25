package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fairshare.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Map<String, Any>?>(null)
    val user: StateFlow<Map<String, Any>?> = _user.asStateFlow()

    // Save or update user profile
    fun saveUser(userId: String, name: String, email: String, profilePicUrl: String? = null) {
        val data = mutableMapOf(
            "name" to name,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )
        profilePicUrl?.let { data["profilePicUrl"] = it }

        userRepository.saveUser(userId, data) { success ->
            if (success) getUser(userId)
        }
    }

    // Load user data
    fun getUser(userId: String) {
        userRepository.getUser(userId) { data ->
            _user.value = data
        }
    }

    fun deleteUser(userId: String) {
        userRepository.deleteUser(userId) { success ->
            if (success) _user.value = null
        }
    }

    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            getUser(uid)
        }
    }
}
