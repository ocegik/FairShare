package com.example.fairshare.repository


import android.util.Log
import com.example.fairshare.data.firebase.FirestoreService
import com.example.fairshare.data.models.DebtData
import javax.inject.Inject

class DebtRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    companion object {
        private const val COLLECTION_PATH = "debts"
        private const val TAG = "DebtRepository"
    }

    // Add a new debt
    suspend fun addDebt(debt: DebtData): Result<DebtData> {
        val docId = debt.id.ifEmpty {
            firestoreService.generateDocumentId(COLLECTION_PATH)
        }

        val debtToSave = if (debt.id.isEmpty()) {
            debt.copy(id = docId)
        } else {
            debt
        }

        return firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = docId,
            data = debtToSave
        ).map { debtToSave }
            .onSuccess {
                Log.d(TAG, "Debt added successfully: $docId")
            }
            .onFailure {
                Log.e(TAG, "Error adding debt: $docId", it)
            }
    }

    // Get a single debt by ID
    suspend fun getDebt(debtId: String): Result<DebtData> {
        return firestoreService.getDocument(
            collectionPath = COLLECTION_PATH,
            documentId = debtId,
            clazz = DebtData::class.java
        ).onSuccess {
            Log.d(TAG, "Debt retrieved: $debtId")
        }.onFailure {
            Log.e(TAG, "Error getting debt: $debtId", it)
        }
    }

    // Get debts where current user owes someone (fromUserId)
    suspend fun getDebtsOwedByUser(userId: String): Result<List<DebtData>> {
        return firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "fromUserId",
            value = userId,
            clazz = DebtData::class.java
        ).onSuccess {
            Log.d(TAG, "Retrieved ${it.size} debts owed by user: $userId")
        }.onFailure {
            Log.e(TAG, "Error getting debts owed by user: $userId", it)
        }
    }

    // Get debts where current user is owed money (toUserId)
    suspend fun getDebtsOwedToUser(userId: String): Result<List<DebtData>> {
        return firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "toUserId",
            value = userId,
            clazz = DebtData::class.java
        ).onSuccess {
            Log.d(TAG, "Retrieved ${it.size} debts owed to user: $userId")
        }.onFailure {
            Log.e(TAG, "Error getting debts owed to user: $userId", it)
        }
    }

    // Get debts for a specific group
    suspend fun getDebtsByGroup(groupId: String): Result<List<DebtData>> {
        return firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "groupId",
            value = groupId,
            clazz = DebtData::class.java
        ).onSuccess {
            Log.d(TAG, "Retrieved ${it.size} debts for group: $groupId")
        }.onFailure {
            Log.e(TAG, "Error getting debts for group: $groupId", it)
        }
    }

    // Update any field (e.g. status = "settled")
    suspend fun updateDebt(
        debtId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = debtId,
            updates = updates
        ).onSuccess {
            Log.d(TAG, "Debt updated: $debtId")
        }.onFailure {
            Log.e(TAG, "Error updating debt: $debtId", it)
        }
    }

    // Delete debt record (optional — you may only allow if cancelled)
    suspend fun deleteDebt(debtId: String): Result<Unit> {
        return firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = debtId
        ).onSuccess {
            Log.d(TAG, "Debt deleted: $debtId")
        }.onFailure {
            Log.e(TAG, "Error deleting debt: $debtId", it)
        }
    }
}
