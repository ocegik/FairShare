package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fairshare.data.models.Group
import com.example.fairshare.repository.GroupRepository
import com.example.fairshare.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository // Assuming you have this
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _userGroups = MutableStateFlow<List<Group>>(emptyList())
    val userGroups: StateFlow<List<Group>> = _userGroups.asStateFlow()

    fun addGroup(group: Group) {
        groupRepository.addGroup(group) { success ->
            if (success) loadGroups()
        }
    }

    fun loadGroups() {
        groupRepository.getAllGroups { list ->
            _groups.value = list
        }
    }

    fun getGroup(groupId: String, onResult: (Group?) -> Unit) {
        groupRepository.getGroup(groupId, onResult)
    }

    fun updateGroup(groupId: String, group: Group) {
        groupRepository.updateGroup(groupId, group) { success ->
            if (success) loadGroups()
        }
    }

    fun deleteGroup(groupId: String) {
        groupRepository.deleteGroup(groupId) { success ->
            if (success) loadGroups()
        }
    }

    fun transferOwnership(groupId: String, newOwnerId: String) {
        getGroup(groupId) { group ->
            group?.let {
                updateGroup(groupId, it.copy(owner = newOwnerId))
            }
        }
    }

    fun removeMember(groupId: String, userId: String) {
        getGroup(groupId) { group ->
            group?.let {
                val updatedMembers = it.members.filter { id -> id != userId }
                updateGroup(groupId, it.copy(members = updatedMembers))

                // Also remove from user's groups list
                userRepository.removeGroupFromUser(userId, groupId)
            }
        }
    }

    // NEW: Efficiently load groups for a specific user using their groups list
    fun loadGroupsForUser(userId: String) {
        userRepository.getUser(userId) { user ->
            user?.let {
                if (it.groups.isNotEmpty()) {
                    groupRepository.getGroupsByIds(it.groups) { groups ->
                        _userGroups.value = groups
                    }
                } else {
                    _userGroups.value = emptyList()
                }
            }
        }
    }

    fun joinGroup(groupId: String, password: String, userId: String, onResult: (Boolean) -> Unit) {
        getGroup(groupId) { group ->
            if (group == null) {
                onResult(false)
                return@getGroup
            }
            if (group.password == password) {
                val updatedMembers = if (!group.members.contains(userId)) {
                    group.members + userId
                } else {
                    group.members
                }
                updateGroup(groupId, group.copy(members = updatedMembers))

                // Add group to user's groups list
                userRepository.addGroupToUser(userId, groupId)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}
