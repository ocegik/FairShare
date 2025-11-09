package com.example.fairshare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.Group
import com.example.fairshare.data.models.GroupMember
import com.example.fairshare.data.models.GroupUiData
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

    private val _uiState = MutableStateFlow<GroupUiState>(GroupUiState.Idle)
    val uiState: StateFlow<GroupUiState> = _uiState.asStateFlow()

    // ------------------------------------------------------------------------
    // UI STATE
    // ------------------------------------------------------------------------

    sealed class GroupUiState {
        object Idle : GroupUiState()
        object Loading : GroupUiState()
        data class Success(val message: String? = null) : GroupUiState()
        data class Error(val message: String) : GroupUiState()
    }

    // ------------------------------------------------------------------------
    // PRIVATE INTERNAL HELPERS
    // ------------------------------------------------------------------------

    private suspend fun loadGroupsInternal() {
        val list = groupRepository.getAllGroups().getOrThrow()
        _groups.value = list
    }

    private suspend fun loadUserGroupsInternal(userId: String) {
        val user = userRepository.getUser(userId).getOrThrow()

        val groups =
            if (user.groups.isNotEmpty())
                groupRepository.getGroupsByIds(user.groups).getOrThrow()
            else emptyList()

        _userGroups.value = groups
    }

    // ------------------------------------------------------------------------
    // PUBLIC FUNCTIONS (UI SHOULD CALL THESE)
    // ------------------------------------------------------------------------

    suspend fun getGroup(groupId: String): Result<Group> {
        return groupRepository.getGroup(groupId)
    }
    fun addGroup(group: Group) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                groupRepository.addGroup(group).getOrThrow()
                loadGroupsInternal()

                _uiState.value = GroupUiState.Success("Group created successfully")
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to create group")
            }
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                val list = groupRepository.getAllGroups().getOrThrow()
                _groups.value = list

                _uiState.value = GroupUiState.Idle
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to load groups")
            }
        }
    }

    fun loadGroupsForUser(userId: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                loadUserGroupsInternal(userId)

                _uiState.value = GroupUiState.Idle
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to load user groups")
            }
        }
    }

    fun updateGroup(groupId: String, group: Group) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                groupRepository.updateGroup(groupId, group).getOrThrow()
                loadGroupsInternal()

                _uiState.value = GroupUiState.Success("Group updated successfully")
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to update group")
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                groupRepository.deleteGroup(groupId).getOrThrow()
                loadGroupsInternal()

                _uiState.value = GroupUiState.Success("Group deleted successfully")
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to delete group")
            }
        }
    }

    fun transferOwnership(groupId: String, newOwnerId: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                val group = groupRepository.getGroup(groupId).getOrThrow()
                groupRepository.updateGroup(groupId, group.copy(owner = newOwnerId))
                    .getOrThrow()

                loadGroupsInternal()

                _uiState.value = GroupUiState.Success("Ownership transferred")
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to transfer ownership")
            }
        }
    }

    fun removeMember(groupId: String, userId: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                val group = groupRepository.getGroup(groupId).getOrThrow()
                val updatedMembers = group.members.filter { it != userId }

                groupRepository.updateGroup(groupId, group.copy(members = updatedMembers))
                    .getOrThrow()

                userRepository.removeGroupFromUser(userId, groupId).getOrThrow()

                _uiState.value = GroupUiState.Success("Member removed")
            }.onFailure { e ->
                _uiState.value =
                    GroupUiState.Error(e.message ?: "Failed to remove member")
            }
        }
    }

    fun joinGroup(groupId: String, password: String, userId: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = GroupUiState.Loading

                val group = groupRepository.getGroup(groupId).getOrThrow()

                if (group.password != password)
                    throw Exception("Invalid password")

                if (!group.members.contains(userId)) {
                    val updatedMembers = group.members + userId
                    groupRepository.updateGroup(groupId, group.copy(members = updatedMembers))
                        .getOrThrow()
                    userRepository.addGroupToUser(userId, groupId).getOrThrow()
                }

                loadUserGroupsInternal(userId)

                _uiState.value = GroupUiState.Success("Joined successfully")
            }.onFailure { e ->
                val msg = when {
                    e.message?.contains("Invalid password") == true -> "Invalid password"
                    e.message?.contains("not found") == true -> "Group not found"
                    else -> "Failed to join group"
                }

                _uiState.value = GroupUiState.Error(msg)
            }
        }
    }

    fun generateGroupId(): String {
        return groupRepository.generateGroupId()
    }

    fun clearUiState() {
        _uiState.value = GroupUiState.Idle
    }

    fun loadInitialUserGroups(userId: String) {
        loadGroupsForUser(userId)
    }

    private suspend fun toGroupMember(userId: String, ownerId: String): GroupMember {
        val user = userRepository.getUser(userId).getOrThrow()
        return GroupMember(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl,
            isOwner = userId == ownerId
        )
    }

    suspend fun getGroupFullDetails(groupId: String): Result<GroupUiData> {
        return runCatching {
            val group = groupRepository.getGroup(groupId).getOrThrow()

            val members = group.members.map { id ->
                toGroupMember(id, group.owner)
            }.sortedByDescending { it.isOwner } // owner first

            GroupUiData(
                group = group,
                members = members
            )
        }
    }
    suspend fun getGroupPreviewMembers(group: Group): List<GroupMember> {
        val previewIds = group.members.take(3)

        return previewIds.map { id ->
            toGroupMember(id, group.owner)
        }
    }

    companion object {
        private const val TAG = "GroupViewModel"
    }
}
