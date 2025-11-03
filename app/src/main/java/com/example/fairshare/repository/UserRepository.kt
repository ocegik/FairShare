package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.firebase.FirestoreService
import com.example.fairshare.data.models.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    companion object {
        private const val COLLECTION_PATH = "users"
        private const val TAG = "UserRepository"
    }

    suspend fun saveUser(user: User): Result<Unit> {
        Log.d(TAG, "Attempting to save user with ID: ${user.uid}")

        val userData = mapOf(
            "uid" to user.uid,
            "displayName" to (user.displayName ?: ""),
            "email" to (user.email ?: ""),
            "photoUrl" to (user.photoUrl ?: ""),
            "groups" to user.groups
        )

        return firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = user.uid,
            data = userData
        ).onSuccess {
            Log.d(TAG, "User successfully saved: ${user.uid}")
        }.onFailure {
            Log.e(TAG, "Error saving user: ${user.uid}", it)
        }
    }

    suspend fun getUser(userId: String): Result<User> {
        Log.d(TAG, "Attempting to get user: $userId")

        return firestoreService.getDocumentAsMap(
            collectionPath = COLLECTION_PATH,
            documentId = userId
        ).mapCatching { map ->
            mapToUser(map) ?: throw Exception("Failed to parse user data")
        }.onSuccess {
            Log.d(TAG, "User successfully retrieved: $userId")
        }.onFailure {
            Log.e(TAG, "Error getting user: $userId", it)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        Log.d(TAG, "Attempting to delete user: $userId")

        return firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = userId
        ).onSuccess {
            Log.d(TAG, "User successfully deleted: $userId")
        }.onFailure {
            Log.e(TAG, "Error deleting user: $userId", it)
        }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        Log.d(TAG, "Attempting to update user: $userId")

        return firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = userId,
            updates = updates
        ).onSuccess {
            Log.d(TAG, "User successfully updated: $userId")
        }.onFailure {
            Log.e(TAG, "Error updating user: $userId", it)
        }
    }

    // Add a group to user's groups list
    suspend fun addGroupToUser(userId: String, groupId: String): Result<Unit> {
        return getUser(userId).mapCatching { user ->
            val currentGroups = user.groups.toMutableList()

            if (currentGroups.contains(groupId)) {
                Log.d(TAG, "User $userId already in group $groupId")
                return@mapCatching
            }

            currentGroups.add(groupId)
            val updates = mapOf("groups" to currentGroups)

            updateUser(userId, updates).getOrThrow()
            Log.d(TAG, "Group $groupId added to user $userId")
        }
    }

    // Remove a group from user's groups list
    suspend fun removeGroupFromUser(userId: String, groupId: String): Result<Unit> {
        return getUser(userId).mapCatching { user ->
            val currentGroups = user.groups.toMutableList()

            if (!currentGroups.remove(groupId)) {
                Log.d(TAG, "User $userId not in group $groupId")
                return@mapCatching
            }

            val updates = mapOf("groups" to currentGroups)
            updateUser(userId, updates).getOrThrow()
            Log.d(TAG, "Group $groupId removed from user $userId")
        }
    }

    // Get all groups for a user
    suspend fun getUserGroups(userId: String): Result<List<String>> {
        return getUser(userId).map { user ->
            user.groups
        }
    }

    fun generateUserId(): String {
        return firestoreService.generateDocumentId(COLLECTION_PATH)
    }

    private fun mapToUser(map: Map<String, Any>): User? {
        return try {
            User(
                uid = map["uid"] as? String ?: "",
                displayName = map["displayName"] as? String,
                email = map["email"] as? String,
                photoUrl = map["photoUrl"] as? String,
                groups = (map["groups"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting map to User: ${e.message}")
            null
        }
    }

    // Subcollection operations
    suspend fun <T : Any> addToUserSubCollection(
        userId: String,
        subCollectionName: String,
        documentId: String,
        data: T
    ): Result<Unit> {
        Log.d(TAG, "Adding document to $subCollectionName for user: $userId")
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        return firestoreService.addDocument(
            collectionPath = fullPath,
            documentId = documentId,
            data = data
        ).onSuccess {
            Log.d(TAG, "Document added to $subCollectionName: $documentId")
        }.onFailure {
            Log.e(TAG, "Error adding document to $subCollectionName", it)
        }
    }

    suspend fun <T> getUserSubCollection(
        userId: String,
        subCollectionName: String,
        clazz: Class<T>
    ): Result<List<T>> {
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        return firestoreService.getAllDocuments(
            collectionPath = fullPath,
            clazz = clazz
        )
    }

    suspend fun <T> getUserSubCollectionDocument(
        userId: String,
        subCollectionName: String,
        documentId: String,
        clazz: Class<T>
    ): Result<T> {
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        return firestoreService.getDocument(
            collectionPath = fullPath,
            documentId = documentId,
            clazz = clazz
        )
    }

    suspend fun updateUserSubCollectionDocument(
        userId: String,
        subCollectionName: String,
        documentId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        Log.d(TAG, "Updating document in $subCollectionName for user: $userId")
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        return firestoreService.updateDocument(
            collectionPath = fullPath,
            documentId = documentId,
            updates = updates
        ).onSuccess {
            Log.d(TAG, "Document updated in $subCollectionName: $documentId")
        }.onFailure {
            Log.e(TAG, "Error updating document in $subCollectionName", it)
        }
    }

    suspend fun deleteUserSubCollectionDocument(
        userId: String,
        subCollectionName: String,
        documentId: String
    ): Result<Unit> {
        Log.d(TAG, "Deleting document from $subCollectionName for user: $userId")
        val fullPath = "$COLLECTION_PATH/$userId/$subCollectionName"

        return firestoreService.deleteDocument(
            collectionPath = fullPath,
            documentId = documentId
        ).onSuccess {
            Log.d(TAG, "Document deleted from $subCollectionName: $documentId")
        }.onFailure {
            Log.e(TAG, "Error deleting document from $subCollectionName", it)
        }
    }
}