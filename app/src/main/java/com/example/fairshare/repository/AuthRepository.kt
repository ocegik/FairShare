package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.firebase.FirebaseAuthService
import com.example.fairshare.data.models.User
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val userRepository: UserRepository
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuthService.getCurrentFirebaseUser() ?: return null
        // Create User from Firebase Auth data
        return User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val result = firebaseAuthService.signInWithCredential(idToken)

        val firebaseUser = result.user
            ?: throw Exception("Firebase user is null after sign-in")

        // Create User object from Firebase Auth data
        val user = User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )

        val isNewUser = result.additionalUserInfo?.isNewUser ?: false

        if (isNewUser) {
            try {
                // Convert User to Map for Firestore
                val userData = mapOf(
                    "uid" to user.uid,
                    "displayName" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "photoUrl" to (user.photoUrl ?: "")
                )

                userRepository.saveUser(user.uid, userData) { success ->
                    if (success) {
                        Log.d(TAG, "New user saved to Firestore: ${user.uid}")
                    } else {
                        Log.e(TAG, "Failed to save new user to Firestore: ${user.uid}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save new user to Firestore: ${user.uid}", e)
                // Continue with sign-in regardless of Firestore save result
            }
        }

        user
    }

    fun signOut() {
        firebaseAuthService.signOut()
    }
}