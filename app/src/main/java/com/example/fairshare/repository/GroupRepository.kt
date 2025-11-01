package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.firebase.FirestoreService
import com.example.fairshare.data.models.Group
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val firestoreService: FirestoreService,
    private val userRepository: UserRepository
) {

    companion object {
        private const val COLLECTION_PATH = "groups"
        private const val USERS_COLLECTION = "users"
        private const val TAG = "GroupRepository"
    }

    fun addGroup(group: Group, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to add group with ID: ${group.groupId}")

        val groupData = mapOf(
            "name" to group.name,
            "owner" to group.owner,
            "createdAt" to group.createdAt,
            "members" to group.members,
            "groupId" to group.groupId,
            "password" to group.password
        )

        firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = group.groupId,
            data = groupData,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Group successfully added: ${group.groupId}")
                    // Update all members' user documents
                    group.members.forEach { userId ->
                        userRepository.addGroupToUser(userId, group.groupId)
                    }
                } else {
                    Log.e(TAG, "Error adding group: ${group.groupId}")
                }
                onResult(success)
            }
        )
    }

    fun getGroup(groupId: String, onResult: (Group?) -> Unit) {
        firestoreService.getDocumentAsMap(
            collectionPath = COLLECTION_PATH,
            documentId = groupId,
            onResult = { map ->
                val group = map?.let { mapToGroup(it) }
                onResult(group)
            }
        )
    }

    fun getAllGroups(onResult: (List<Group>) -> Unit) {
        firestoreService.getAllDocumentsAsMap(
            collectionPath = COLLECTION_PATH,
            onResult = { maps ->
                val groups = maps.mapNotNull { mapToGroup(it) }
                onResult(groups)
            }
        )
    }

    // NEW: Fetch groups by their IDs (from user's groups list)
    fun getGroupsByIds(groupIds: List<String>, onResult: (List<Group>) -> Unit) {
        if (groupIds.isEmpty()) {
            onResult(emptyList())
            return
        }

        val groups = mutableListOf<Group>()
        var completed = 0

        groupIds.forEach { groupId ->
            getGroup(groupId) { group ->
                group?.let { groups.add(it) }
                completed++
                if (completed == groupIds.size) {
                    onResult(groups)
                }
            }
        }
    }

    fun updateGroup(
        groupId: String,
        group: Group,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "Attempting to update group: $groupId")

        // Get old group to compare members
        getGroup(groupId) { oldGroup ->
            val updates = mapOf(
                "name" to group.name,
                "owner" to group.owner,
                "members" to group.members,
                "password" to group.password,
                "createdAt" to group.createdAt
            )

            firestoreService.updateDocument(
                collectionPath = COLLECTION_PATH,
                documentId = groupId,
                updates = updates,
                onResult = { success ->
                    if (success) {
                        Log.d(TAG, "Group successfully updated: $groupId")

                        // Sync member changes if members list changed
                        oldGroup?.let { old ->
                            val removedMembers = old.members.filter { !group.members.contains(it) }
                            val addedMembers = group.members.filter { !old.members.contains(it) }

                            removedMembers.forEach { userId ->
                                userRepository.removeGroupFromUser(userId, groupId)
                            }

                            addedMembers.forEach { userId ->
                                userRepository.addGroupToUser(userId, groupId)
                            }
                        }
                    } else {
                        Log.e(TAG, "Error updating group: $groupId")
                    }
                    onResult(success)
                }
            )
        }
    }

    fun deleteGroup(groupId: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to delete group: $groupId")

        // First get the group to know which users to update
        getGroup(groupId) { group ->
            group?.let {
                // Remove group from all members' user documents
                it.members.forEach { userId ->
                    userRepository.removeGroupFromUser(userId, groupId)
                }
            }

            // Then delete the group
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
    }

    fun generateGroupId(): String {
        return firestoreService.generateDocumentId(COLLECTION_PATH)
    }

    private fun mapToGroup(map: Map<String, Any>): Group? {
        return try {
            Group(
                groupId = map["groupId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                owner = map["owner"] as? String ?: "",
                password = map["password"] as? String ?: "",
                members = (map["members"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting map to Group: ${e.message}")
            null
        }
    }
}