package com.example.fairshare.feature.auth.data

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun getCurrentFirebaseUser() = auth.currentUser

    suspend fun signInWithCredential(idToken: String): AuthResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await()
    }

    fun signOut() = auth.signOut()
}