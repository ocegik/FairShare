package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.Group
import com.example.fairshare.repository.GroupRepository
import com.example.fairshare.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _userGroups = MutableStateFlow<List<Group>>(emptyList())
    val userGroups: StateFlow<List<Group>> = _userGroups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Add group
    fun addGroup(group: Group, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            groupRepository.addGroup(group)
                .onSuccess {
                    Log.d(TAG, "Group added successfully")
                    loadGroups()
                    onComplete(Result.success(Unit))
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to add group", e)
                    _error.value = e.message
                    onComplete(Result.failure(e))
                }
            _isLoading.value = false
        }
    }

    // Load all groups
    fun loadGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            groupRepository.getAllGroups()
                .onSuccess { list ->
                    _groups.value = list
                    Log.d(TAG, "Loaded ${list.size} groups")
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to load groups", e)
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    // Get single group
    suspend fun getGroup(groupId: String): Result<Group> {
        return groupRepository.getGroup(groupId)
    }

    // Update group
    fun updateGroup(groupId: String, group: Group, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            groupRepository.updateGroup(groupId, group)
                .onSuccess {
                    Log.d(TAG, "Group updated successfully")
                    loadGroups()
                    onComplete(Result.success(Unit))
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to update group", e)
                    _error.value = e.message
                    onComplete(Result.failure(e))
                }
            _isLoading.value = false
        }
    }

    // Delete group
    fun deleteGroup(groupId: String, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            groupRepository.deleteGroup(groupId)
                .onSuccess {
                    Log.d(TAG, "Group deleted successfully")
                    loadGroups()
                    onComplete(Result.success(Unit))
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to delete group", e)
                    _error.value = e.message
                    onComplete(Result.failure(e))
                }
            _isLoading.value = false
        }
    }

    // Transfer ownership
    fun transferOwnership(groupId: String, newOwnerId: String, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            groupRepository.getGroup(groupId)
                .onSuccess { group ->
                    updateGroup(groupId, group.copy(owner = newOwnerId), onComplete)
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to transfer ownership", e)
                    _error.value = e.message
                    onComplete(Result.failure(e))
                }
        }
    }

    // Remove member from group
    fun removeMember(groupId: String, userId: String, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            groupRepository.getGroup(groupId)
                .onSuccess { group ->
                    val updatedMembers = group.members.filter { id -> id != userId }
                    updateGroup(groupId, group.copy(members = updatedMembers)) { result ->
                        if (result.isSuccess) {
                            // Also remove from user's groups list
                            viewModelScope.launch {
                                userRepository.removeGroupFromUser(userId, groupId)
                                    .onFailure { e ->
                                        Log.e(TAG, "Failed to remove group from user", e)
                                    }
                            }
                        }
                        onComplete(result)
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to remove member", e)
                    _error.value = e.message
                    onComplete(Result.failure(e))
                }
        }
    }

    // Load groups for a specific user
    fun loadGroupsForUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getUser(userId)
                .onSuccess { user ->
                    if (user.groups.isNotEmpty()) {
                        groupRepository.getGroupsByIds(user.groups)
                            .onSuccess { groups ->
                                _userGroups.value = groups
                                Log.d(TAG, "Loaded ${groups.size} groups for user $userId")
                            }
                            .onFailure { e ->
                                Log.e(TAG, "Failed to load user groups", e)
                                _error.value = e.message
                            }
                    } else {
                        _userGroups.value = emptyList()
                        Log.d(TAG, "User has no groups")
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to load user", e)
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    // Join group with password
    fun joinGroup(
        groupId: String,
        password: String,
        userId: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            groupRepository.getGroup(groupId)
                .onSuccess { group ->
                    if (group.password != password) {
                        Log.d(TAG, "Invalid password for group $groupId")
                        _error.value = "Invalid password"
                        onResult(Result.failure(Exception("Invalid password")))
                        _isLoading.value = false
                        return@onSuccess
                    }

                    val updatedMembers = if (!group.members.contains(userId)) {
                        group.members + userId
                    } else {
                        Log.d(TAG, "User already in group")
                        group.members
                    }

                    groupRepository.updateGroup(groupId, group.copy(members = updatedMembers))
                        .onSuccess {
                            // Add group to user's groups list
                            viewModelScope.launch {
                                userRepository.addGroupToUser(userId, groupId)
                                    .onSuccess {
                                        Log.d(TAG, "User joined group successfully")
                                        loadGroupsForUser(userId)
                                        onResult(Result.success(Unit))
                                    }
                                    .onFailure { e ->
                                        Log.e(TAG, "Failed to add group to user", e)
                                        onResult(Result.failure(e))
                                    }
                            }
                        }
                        .onFailure { e ->
                            Log.e(TAG, "Failed to join group", e)
                            _error.value = e.message
                            onResult(Result.failure(e))
                        }
                }
                .onFailure { e ->
                    Log.e(TAG, "Group not found", e)
                    _error.value = "Group not found"
                    onResult(Result.failure(e))
                }

            _isLoading.value = false
        }
    }

    fun generateGroupId(): String {
        return groupRepository.generateGroupId()
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val TAG = "GroupViewModel"
    }
}
