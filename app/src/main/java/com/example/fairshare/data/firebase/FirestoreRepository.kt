package com.example.fairshare.data.firebase

import com.example.fairshare.ui.components.ExpenseData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val groupsCollection = firestore.collection("groups")
    private val usersCollection = firestore.collection("users")
    private val expenseCollection = firestore.collection("expenses")


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
    fun addExpense(expense: ExpenseData, onResult: (Boolean) -> Unit) {
        val docId = expense.id.ifEmpty { expenseCollection.document().id }
        expenseCollection.document(docId)
            .set(expense)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getExpense(expenseId: String, onResult: (ExpenseData?) -> Unit) {
        expenseCollection.document(expenseId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.toObject(ExpenseData::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getExpensesByUser(userId: String, onResult: (List<ExpenseData>) -> Unit) {
        expenseCollection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { query ->
                val list = query.documents.mapNotNull { it.toObject(ExpenseData::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getExpensesByGroup(groupId: String, onResult: (List<ExpenseData>) -> Unit) {
        expenseCollection.whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { query ->
                val list = query.documents.mapNotNull { it.toObject(ExpenseData::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateExpense(expenseId: String, updates: Map<String, Any>, onResult: (Boolean) -> Unit) {
        expenseCollection.document(expenseId)
            .update(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteExpense(expenseId: String, onResult: (Boolean) -> Unit) {
        expenseCollection.document(expenseId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
