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
            photoUrl = firebaseUser.photoUrl?.toString(),
            groups = emptyList()
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
            photoUrl = firebaseUser.photoUrl?.toString(),
            groups = emptyList()
        )

        val isNewUser = result.additionalUserInfo?.isNewUser ?: false

        if (isNewUser) {
            // Save new user to Firestore
            userRepository.saveUser(user)
                .onSuccess {
                    Log.d(TAG, "New user saved to Firestore: ${user.uid}")
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to save new user to Firestore: ${user.uid}", e)
                    // Continue with sign-in regardless of Firestore save result
                }
        } else {
            // Existing user - optionally fetch from Firestore to get groups
            userRepository.getUser(user.uid)
                .onSuccess { firestoreUser ->
                    Log.d(TAG, "Existing user loaded from Firestore: ${user.uid}")
                    return@runCatching firestoreUser // Return user with groups
                }
                .onFailure { e ->
                    Log.w(TAG, "Could not load user from Firestore, using auth data: ${user.uid}", e)
                    // Continue with basic user data from auth
                }
        }

        user
    }

    fun signOut() {
        firebaseAuthService.signOut()
        Log.d(TAG, "User signed out")
    }
}