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

    fun saveUser(user: User, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to save user with ID: ${user.uid}")

        val userData = buildMap {
            put("uid", user.uid)
            user.displayName?.let { put("displayName", it) }
            user.email?.let { put("email", it) }
            user.photoUrl?.let { put("photoUrl", it) }
            put("groups", user.groups)
        }

        firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = user.uid,
            data = userData,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "User successfully saved: ${user.uid}")
                } else {
                    Log.e(TAG, "Error saving user: ${user.uid}")
                }
                onResult(success)
            }
        )
    }

    fun getUser(userId: String, onResult: (User?) -> Unit) {
        firestoreService.getDocumentAsMap(
            collectionPath = COLLECTION_PATH,
            documentId = userId,
            onResult = { map ->
                val user = map?.let { mapToUser(it) }
                onResult(user)
            }
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
        user: User,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Attempting to update user: $userId")

        val updates = buildMap{
            user.displayName?.let { put("displayName", it) }
            user.email?.let { put("email", it) }
            user.photoUrl?.let { put("photoUrl", it) }
            put("groups", user.groups)
        }

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

    // Add a group to user's groups list
    fun addGroupToUser(userId: String, groupId: String, onResult: (Boolean) -> Unit = {}) {
        getUser(userId) { user ->
            user?.let {
                val currentGroups = it.groups.toMutableList()
                if (!currentGroups.contains(groupId)) {
                    currentGroups.add(groupId)
                    val updates = mapOf("groups" to currentGroups)

                    firestoreService.updateDocument(
                        collectionPath = COLLECTION_PATH,
                        documentId = userId,
                        updates = updates,
                        onResult = { success ->
                            if (success) {
                                Log.d(TAG, "Group $groupId added to user $userId")
                            } else {
                                Log.e(TAG, "Failed to add group $groupId to user $userId")
                            }
                            onResult(success)
                        }
                    )
                } else {
                    Log.d(TAG, "User $userId already in group $groupId")
                    onResult(true)
                }
            } ?: run {
                Log.e(TAG, "User $userId not found")
                onResult(false)
            }
        }
    }

    // Remove a group from user's groups list
    fun removeGroupFromUser(userId: String, groupId: String, onResult: (Boolean) -> Unit = {}) {
        getUser(userId) { user ->
            user?.let {
                val currentGroups = it.groups.toMutableList()
                if (currentGroups.remove(groupId)) {
                    val updates = mapOf("groups" to currentGroups)

                    firestoreService.updateDocument(
                        collectionPath = COLLECTION_PATH,
                        documentId = userId,
                        updates = updates,
                        onResult = { success ->
                            if (success) {
                                Log.d(TAG, "Group $groupId removed from user $userId")
                            } else {
                                Log.e(TAG, "Failed to remove group $groupId from user $userId")
                            }
                            onResult(success)
                        }
                    )
                } else {
                    Log.d(TAG, "User $userId not in group $groupId")
                    onResult(true)
                }
            } ?: run {
                Log.e(TAG, "User $userId not found")
                onResult(false)
            }
        }
    }

    // Get all groups for a user
    fun getUserGroups(userId: String, onResult: (List<String>) -> Unit) {
        getUser(userId) { user ->
            onResult(user?.groups ?: emptyList())
        }
    }

    fun generateUserId(): String {
        return firestoreService.generateDocumentId(COLLECTION_PATH)
    }

    private fun mapToUser(map: Map<String, Any>): User? {
        return try {
            User(
                uid = map["userId"] as? String ?: "",
                displayName = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                photoUrl = map["profilePhoto"] as? String ?: "",
                groups = (map["groups"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting map to User: ${e.message}")
            null
        }
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