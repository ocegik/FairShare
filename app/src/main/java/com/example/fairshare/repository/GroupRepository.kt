package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.firebase.FirestoreService
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    companion object {
        private const val COLLECTION_PATH = "groups"
        private const val TAG = "GroupRepository"
    }

    fun addGroup(groupId: String, groupData: Map<String, Any>, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to add group with ID: $groupId")

        firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = groupId,
            data = groupData,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Group successfully added: $groupId")
                } else {
                    Log.e(TAG, "Error adding group: $groupId")
                }
                onResult(success)
            }
        )
    }

    fun getGroup(groupId: String, onResult: (Map<String, Any>?) -> Unit) {
        firestoreService.getDocumentAsMap(
            collectionPath = COLLECTION_PATH,
            documentId = groupId,
            onResult = onResult
        )
    }

    fun getAllGroups(onResult: (List<Map<String, Any>>) -> Unit) {
        firestoreService.getAllDocumentsAsMap(
            collectionPath = COLLECTION_PATH,
            onResult = onResult
        )
    }

    fun updateGroup(
        groupId: String,
        updates: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Attempting to update group: $groupId")

        firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = groupId,
            updates = updates,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Group successfully updated: $groupId")
                } else {
                    Log.e(TAG, "Error updating group: $groupId")
                }
                onResult(success)
            }
        )
    }

    fun deleteGroup(groupId: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to delete group: $groupId")

        firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = groupId,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Group successfully deleted: $groupId")
                } else {
                    Log.e(TAG, "Error deleting group: $groupId")
                }
                onResult(success)
            }
        )
    }

    fun generateGroupId(): String {
        return firestoreService.generateDocumentId(COLLECTION_PATH)
    }
}