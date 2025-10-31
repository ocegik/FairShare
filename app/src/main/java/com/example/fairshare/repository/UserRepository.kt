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

    // Subcollection operations
    fun <T : Any> addToUserSubCollection(
        userId: String,
        subCollectionName: String,
        documentId: String,
        data: T,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Adding document to $subCollectionName for user: $userId")
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        firestoreService.addDocument(
            collectionPath = fullPath,
            documentId = documentId,
            data = data,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Document added to $subCollectionName: $documentId")
                } else {
                    Log.e(TAG, "Error adding document to $subCollectionName")
                }
                onResult(success)
            }
        )
    }

    fun <T> getUserSubCollection(
        userId: String,
        subCollectionName: String,
        clazz: Class<T>,
        onResult: (List<T>) -> Unit
    ) {
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        firestoreService.getAllDocuments(
            collectionPath = fullPath,
            clazz = clazz,
            onResult = onResult
        )
    }

    fun <T> getUserSubCollectionDocument(
        userId: String,
        subCollectionName: String,
        documentId: String,
        clazz: Class<T>,
        onResult: (T?) -> Unit
    ) {
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        firestoreService.getDocument(
            collectionPath = fullPath,
            documentId = documentId,
            clazz = clazz,
            onResult = onResult
        )
    }

    fun updateUserSubCollectionDocument(
        userId: String,
        subCollectionName: String,
        documentId: String,
        updates: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Updating document in $subCollectionName for user: $userId")
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        firestoreService.updateDocument(
            collectionPath = fullPath,
            documentId = documentId,
            updates = updates,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Document updated in $subCollectionName: $documentId")
                } else {
                    Log.e(TAG, "Error updating document in $subCollectionName")
                }
                onResult(success)
            }
        )
    }

    fun deleteUserSubCollectionDocument(
        userId: String,
        subCollectionName: String,
        documentId: String,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Deleting document from $subCollectionName for user: $userId")
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        firestoreService.deleteDocument(
            collectionPath = fullPath,
            documentId = documentId,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Document deleted from $subCollectionName: $documentId")
                } else {
                    Log.e(TAG, "Error deleting document from $subCollectionName")
                }
                onResult(success)
            }
        )
    }
}