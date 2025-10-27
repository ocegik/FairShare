package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> = suspendCoroutine { continuation ->
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user
                    if (firebaseUser == null) {
                        continuation.resume(Result.failure(Exception("Firebase user is null after sign-in")))
                        return@addOnSuccessListener
                    }

                    val user = User(
                        uid = firebaseUser.uid,
                        displayName = firebaseUser.displayName,
                        email = firebaseUser.email,
                        photoUrl = firebaseUser.photoUrl?.toString()
                    )

                    // Check if this is a new user
                    val isNewUser = result.additionalUserInfo?.isNewUser ?: false

                    if (isNewUser) {
                        // Save new user to Firestore
                        val userData = mapOf(
                            "uid" to user.uid,
                            "displayName" to (user.displayName ?: ""),
                            "email" to (user.email ?: ""),
                            "photoUrl" to (user.photoUrl ?: ""),
                            "createdAt" to System.currentTimeMillis()
                        )

                        userRepository.saveUser(user.uid, userData) { success ->
                            if (success) {
                                Log.d(TAG, "New user saved to Firestore: ${user.uid}")
                            } else {
                                Log.e(TAG, "Failed to save new user to Firestore: ${user.uid}")
                            }
                            // Continue with sign-in regardless of Firestore save result
                            continuation.resume(Result.success(user))
                        }
                    } else {
                        continuation.resume(Result.success(user))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    fun signOut() {
        auth.signOut()
    }
}