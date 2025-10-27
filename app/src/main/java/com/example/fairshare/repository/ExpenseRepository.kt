package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.data.firebase.FirestoreService
import com.example.fairshare.ui.components.ExpenseData
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    companion object {
        private const val COLLECTION_PATH = "expenses"
        private const val TAG = "ExpenseRepository"
    }

    fun addExpense(expense: ExpenseData, onResult: (Boolean) -> Unit) {
        val docId = expense.id.ifEmpty {
            firestoreService.generateDocumentId(COLLECTION_PATH)
        }

        Log.d(TAG, "Attempting to save expense with ID: $docId")

        val expenseToSave = if (expense.id.isEmpty()) {
            expense.copy(id = docId)
        } else {
            expense
        }

        firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = docId,
            data = expenseToSave,
            onResult = { success ->
                if (success) {
                    Log.d(TAG, "Expense successfully added: $expenseToSave")
                } else {
                    Log.e(TAG, "Error adding expense")
                }
                onResult(success)
            }
        )
    }

    fun getExpense(expenseId: String, onResult: (ExpenseData?) -> Unit) {
        firestoreService.getDocument(
            collectionPath = COLLECTION_PATH,
            documentId = expenseId,
            clazz = ExpenseData::class.java,
            onResult = onResult
        )
    }

    fun getExpensesByUser(userId: String, onResult: (List<ExpenseData>) -> Unit) {
        firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "userId",
            value = userId,
            clazz = ExpenseData::class.java,
            onResult = onResult
        )
    }

    fun getExpensesByGroup(groupId: String, onResult: (List<ExpenseData>) -> Unit) {
        firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "groupId",
            value = groupId,
            clazz = ExpenseData::class.java,
            onResult = onResult
        )
    }

    fun updateExpense(
        expenseId: String,
        updates: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = expenseId,
            updates = updates,
            onResult = onResult
        )
    }

    fun deleteExpense(expenseId: String, onResult: (Boolean) -> Unit) {
        firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = expenseId,
            onResult = onResult
        )
    }
}