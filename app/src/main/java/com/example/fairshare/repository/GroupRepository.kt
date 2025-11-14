package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.core.firebase.FirestoreService
import com.example.fairshare.core.data.models.Group
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val firestoreService: FirestoreService,
    private val userRepository: UserRepository
) {

    companion object {
        private const val COLLECTION_PATH = "groups"
        private const val TAG = "GroupRepository"
    }

    suspend fun addGroup(group: Group): Result<Unit> {
        Log.d(TAG, "Attempting to add group with ID: ${group.groupId}")

        val groupData = mapOf(
            "name" to group.name,
            "owner" to group.owner,
            "createdAt" to group.createdAt,
            "members" to group.members,
            "groupId" to group.groupId,
            "password" to group.password
        )

        return firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = group.groupId,
            data = groupData
        ).onSuccess {
            Log.d(TAG, "Group successfully added: ${group.groupId}")

            // Update all members' user documents
            group.members.forEach { userId ->
                userRepository.addGroupToUser(userId, group.groupId)
                    .onFailure { e ->
                        Log.e(TAG, "Failed to add group to user $userId", e)
                    }
            }
        }.onFailure {
            Log.e(TAG, "Error adding group: ${group.groupId}", it)
        }
    }

    suspend fun getGroup(groupId: String): Result<Group> {
        return firestoreService.getDocumentAsMap(
            collectionPath = COLLECTION_PATH,
            documentId = groupId
        ).mapCatching { map ->
            mapToGroup(map) ?: throw Exception("Failed to parse group data")
        }.onSuccess {
            Log.d(TAG, "Group retrieved: $groupId")
        }.onFailure {
            Log.e(TAG, "Error getting group: $groupId", it)
        }
    }

    suspend fun getAllGroups(): Result<List<Group>> {
        return firestoreService.getAllDocumentsAsMap(
            collectionPath = COLLECTION_PATH
        ).map { maps ->
            maps.mapNotNull { mapToGroup(it) }
        }.onSuccess {
            Log.d(TAG, "Retrieved ${it.size} groups")
        }.onFailure {
            Log.e(TAG, "Error getting all groups", it)
        }
    }

    // NEW: Fetch groups by their IDs (from user's groups list)
    suspend fun getGroupsByIds(groupIds: List<String>): Result<List<Group>> {
        if (groupIds.isEmpty()) {
            return Result.success(emptyList())
        }

        return runCatching {
            // Fetch all groups concurrently using coroutines
            val groups = groupIds.mapNotNull { groupId ->
                getGroup(groupId).getOrNull()
            }
            groups
        }.onSuccess {
            Log.d(TAG, "Retrieved ${it.size} groups by IDs")
        }.onFailure {
            Log.e(TAG, "Error getting groups by IDs", it)
        }
    }

    suspend fun updateGroup(
        groupId: String,
        group: Group
    ): Result<Unit> {
        Log.d(TAG, "Attempting to update group: $groupId")

        return runCatching {
            // Get old group to compare members
            val oldGroup = getGroup(groupId).getOrThrow()

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
                updates = updates
            ).getOrThrow()

            Log.d(TAG, "Group successfully updated: $groupId")

            // Sync member changes if members list changed
            val removedMembers = oldGroup.members.filter { !group.members.contains(it) }
            val addedMembers = group.members.filter { !oldGroup.members.contains(it) }

            removedMembers.forEach { userId ->
                userRepository.removeGroupFromUser(userId, groupId)
                    .onFailure { e ->
                        Log.e(TAG, "Failed to remove group from user $userId", e)
                    }
            }

            addedMembers.forEach { userId ->
                userRepository.addGroupToUser(userId, groupId)
                    .onFailure { e ->
                        Log.e(TAG, "Failed to add group to user $userId", e)
                    }
            }
        }.onFailure {
            Log.e(TAG, "Error updating group: $groupId", it)
        }
    }

    suspend fun deleteGroup(groupId: String): Result<Int> {
        Log.d(TAG, "Attempting to delete group: $groupId")

        return runCatching {
            // First get the group to know which users to update
            val group = getGroup(groupId).getOrThrow()

            // Remove group from all members' user documents
            group.members.forEach { userId ->
                userRepository.removeGroupFromUser(userId, groupId)
                    .onFailure { e ->
                        Log.e(TAG, "Failed to remove group from user $userId", e)
                    }
            }

            // Then delete the group
            firestoreService.deleteDocument(
                collectionPath = COLLECTION_PATH,
                documentId = groupId
            ).getOrThrow()

            Log.d(TAG, "Group successfully deleted: $groupId")
        }.onFailure {
            Log.e(TAG, "Error deleting group: $groupId", it)
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