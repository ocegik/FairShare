package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fairshare.data.firebase.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: FirestoreRepository
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

        repository.saveUser(userId, data) { success ->
            if (success) getUser(userId)
        }
    }

    // Load user data
    fun getUser(userId: String) {
        repository.getUser(userId) { data ->
            _user.value = data
        }
    }


    fun deleteUser(userId: String) {
        repository.deleteUser(userId) { success ->
            if (success) _user.value = null
        }
    }
    fun loadCurrentUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            getUser(uid)
        }
    }

}
