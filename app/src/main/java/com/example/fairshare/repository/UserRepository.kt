package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.firebase.FirestoreService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    companion object {
        private const val COLLECTION_PATH = "users"
        private const val TAG = "UserRepository"
    }

    fun saveUser(
        userId: String,
        userData: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Attempting to save user with ID: $userId")

        firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = userId,
            data = userData,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "User successfully saved: $userId")
                } else {
                    Log.e(TAG, "Error saving user: $userId")
                }
                onResult(success)
            }
        )
    }

    fun getUser(userId: String, onResult: (Map<String, Any>?) -> Unit) {
        firestoreService.getDocumentAsMap(
            collectionPath = COLLECTION_PATH,
            documentId = userId,
            onResult = onResult
        )
    }

    fun deleteUser(userId: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to delete user: $userId")

        firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = userId,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "User successfully deleted: $userId")
                } else {
                    Log.e(TAG, "Error deleting user: $userId")
                }
                onResult(success)
            }
        )
    }

    fun updateUser(
        userId: String,
        updates: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Attempting to update user: $userId")

        firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = userId,
            updates = updates,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "User successfully updated: $userId")
                } else {
                    Log.e(TAG, "Error updating user: $userId")
                }
                onResult(success)
            }
        )
    }

    fun generateUserId(): String {
        return firestoreService.generateDocumentId(COLLECTION_PATH)
    }
}