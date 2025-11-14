package com.example.fairshare.repository

import android.util.Log
import com.example.fairshare.core.firebase.FirestoreService
import com.example.fairshare.core.data.models.ExpenseData
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    companion object {
        private const val COLLECTION_PATH = "expenses"
        private const val TAG = "ExpenseRepository"
    }

    suspend fun addExpense(expense: ExpenseData): Result<ExpenseData> {
        val docId = expense.id.ifEmpty {
            firestoreService.generateDocumentId(COLLECTION_PATH)
        }

        val expenseToSave = if (expense.id.isEmpty()) {
            expense.copy(id = docId)
        } else {
            expense
        }

        return firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = docId,
            data = expenseToSave
        ).map { expenseToSave }
            .onSuccess {
                Log.d(TAG, "Expense added successfully: $docId")
            }
            .onFailure {
                Log.e(TAG, "Error adding expense: $docId", it)
            }
    }

    suspend fun getExpense(expenseId: String): Result<ExpenseData> {
        return firestoreService.getDocument(
            collectionPath = COLLECTION_PATH,
            documentId = expenseId,
            clazz = ExpenseData::class.java
        ).onSuccess {
            Log.d(TAG, "Expense retrieved: $expenseId")
        }.onFailure {
            Log.e(TAG, "Error getting expense: $expenseId", it)
        }
    }

    suspend fun getExpensesByUser(userId: String): Result<List<ExpenseData>> {
        return firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "userId",
            value = userId,
            clazz = ExpenseData::class.java
        ).onSuccess {
            Log.d(TAG, "Retrieved ${it.size} expenses for user: $userId")
        }.onFailure {
            Log.e(TAG, "Error getting expenses for user: $userId", it)
        }
    }

    suspend fun getExpensesByGroup(groupId: String): Result<List<ExpenseData>> {
        return firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "groupId",
            value = groupId,
            clazz = ExpenseData::class.java
        ).onSuccess {
            Log.d(TAG, "Retrieved ${it.size} expenses for group: $groupId")
        }.onFailure {
            Log.e(TAG, "Error getting expenses for group: $groupId", it)
        }
    }

    suspend fun updateExpense(
        expenseId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = expenseId,
            updates = updates
        ).onSuccess {
            Log.d(TAG, "Expense updated: $expenseId")
        }.onFailure {
            Log.e(TAG, "Error updating expense: $expenseId", it)
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = expenseId
        ).onSuccess {
            Log.d(TAG, "Expense deleted: $expenseId")
        }.onFailure {
            Log.e(TAG, "Error deleting expense: $expenseId", it)
        }
    }
}