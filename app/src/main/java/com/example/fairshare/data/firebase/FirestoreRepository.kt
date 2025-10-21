package com.example.fairshare.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FirestoreRepository {

    private val db = Firebase.firestore
    private val groupsCollection = db.collection("groups")
    private val usersCollection = db.collection("users")


    fun addGroup(groupId: String, groupData: Map<String, Any>, onResult: (Boolean) -> Unit) {
        groupsCollection.document(groupId)
            .set(groupData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Get a single group
    fun getGroup(groupId: String, onResult: (Map<String, Any>?) -> Unit) {
        groupsCollection.document(groupId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) onResult(snapshot.data)
                else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    // Get all groups
    fun getAllGroups(onResult: (List<Map<String, Any>>) -> Unit) {
        groupsCollection.get()
            .addOnSuccessListener { query ->
                val list = query.documents.mapNotNull { it.data }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Update group
    fun updateGroup(groupId: String, updates: Map<String, Any>, onResult: (Boolean) -> Unit) {
        groupsCollection.document(groupId)
            .update(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Delete group
    fun deleteGroup(groupId: String, onResult: (Boolean) -> Unit) {
        groupsCollection.document(groupId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun saveUser(
        userId: String,
        userData: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        usersCollection.document(userId)
            .set(userData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Get single user
    fun getUser(userId: String, onResult: (Map<String, Any>?) -> Unit) {
        usersCollection.document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) onResult(snapshot.data)
                else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    // Optional: delete user
    fun deleteUser(userId: String, onResult: (Boolean) -> Unit) {
        usersCollection.document(userId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
