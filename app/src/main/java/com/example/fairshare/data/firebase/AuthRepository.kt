package com.example.fairshare.data.firebase

import com.example.fairshare.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth)
{
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()

        val firebaseUser = result.user
            ?: throw Exception("Firebase user is null after sign-in")

        User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    fun signOut() {
        auth.signOut()
    }
}
