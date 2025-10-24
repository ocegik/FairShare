package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fairshare.data.firebase.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val groups: StateFlow<List<Map<String, Any>>> = _groups.asStateFlow()

    fun addGroup(groupId: String, name: String, owner: String, members: List<String>, roomPassword: String) {
        val data = mapOf(
            "name" to name,
            "owner" to owner,
            "createdAt" to System.currentTimeMillis(),
            "members" to members,
            "groupID" to groupId,
            "roomPassword" to roomPassword
        )
        repository.addGroup(groupId, data) { success ->
            if (success) loadGroups() // refresh list after adding
        }
    }

    fun loadGroups() {
        repository.getAllGroups { list ->
            _groups.value = list
        }
    }

    fun getGroup(groupId: String, onResult: (Map<String, Any>?) -> Unit) {
        repository.getGroup(groupId, onResult)
    }

    fun updateGroup(groupId: String, updates: Map<String, Any>) {
        repository.updateGroup(groupId, updates) { success ->
            if (success) loadGroups()
        }
    }

    fun deleteGroup(groupId: String) {
        repository.deleteGroup(groupId) { success ->
            if (success) loadGroups()
        }
    }
    fun transferOwnership(groupId: String, newOwnerId: String) {
        updateGroup(groupId, mapOf("owner" to newOwnerId))
    }

    fun removeMember(groupId: String, userId: String) {
        getGroup(groupId) { group ->
            group?.let {
                val members = (it["members"] as List<*>).toMutableList()
                members.remove(userId)
                updateGroup(groupId, mapOf("members" to members))
            }
        }
    }

    fun getGroupsForUser(userId: String, onResult: (List<Map<String, Any>>) -> Unit) {
        repository.getAllGroups { allGroups ->
            val filtered = allGroups.filter { (it["members"] as List<*>).contains(userId) }
            onResult(filtered)
        }
    }

    fun joinGroup(groupId: String, password: String, userId: String, onResult: (Boolean) -> Unit) {
        getGroup(groupId) { group ->
            if (group == null) { onResult(false); return@getGroup }
            if (group["roomPassword"] == password) {
                val members = (group["members"] as List<*>).toMutableList()
                if (!members.contains(userId)) members.add(userId)
                updateGroup(groupId, mapOf("members" to members))
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}
