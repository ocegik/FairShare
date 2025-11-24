package com.example.fairshare.feature.auth.repository

import android.util.Log
import com.example.fairshare.core.data.models.User
import com.example.fairshare.feature.auth.data.FirebaseAuthService
import com.example.fairshare.repository.UserRepository
import kotlinx.coroutines.delay
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
            username = "",
            groups = emptyList()
        )
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val result = firebaseAuthService.signInWithCredential(idToken)
        val firebaseUser = result.user ?: throw Exception("Firebase user is null after sign-in")

        val baseUser = User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: "",
            groups = emptyList(),
            username = "",
            bookMarkedGroup = ""
        )

        val isNewUser = result.additionalUserInfo?.isNewUser ?: false

        if (isNewUser) {
            // --- STEP 1: Write full user doc before returning ---
            userRepository.saveUser(baseUser).onFailure {
                throw Exception("Failed to initialize Firestore user: ${it.message}")
            }

            // --- STEP 2: Double-check Firestore has the doc ---
            repeat(3) { attempt ->
                val firestoreUser = userRepository.getUser(baseUser.uid).getOrNull()
                if (firestoreUser != null && !firestoreUser.displayName.isNullOrEmpty()) {
                    Log.d(TAG, "✅ Firestore confirmed user initialized")
                    return@runCatching firestoreUser
                }
                Log.d(TAG, "Firestore doc not ready yet, retry ${attempt + 1}")
                delay(400) // wait a bit for Firestore sync
            }

            // fallback if Firestore hasn’t synced yet
            Log.w(TAG, "⚠️ Firestore user not yet readable, returning baseUser")
            return@runCatching baseUser
        } else {
            // --- Existing user path ---
            return@runCatching userRepository.getUser(baseUser.uid).getOrElse {
                Log.w(TAG, "Could not load Firestore user; fallback to baseUser")
                baseUser
            }
        }
    }



    fun signOut() {
        firebaseAuthService.signOut()
        Log.d(TAG, "User signed out")
    }
}