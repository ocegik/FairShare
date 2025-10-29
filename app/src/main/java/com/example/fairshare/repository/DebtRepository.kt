package com.example.fairshare.repository


import com.example.fairshare.data.firebase.FirestoreService
import com.example.fairshare.data.models.DebtData
import javax.inject.Inject

class DebtRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    companion object {
        private const val COLLECTION_PATH = "debts"
    }

    // Add a new debt
    fun addDebt(debt: DebtData, onResult: (Boolean) -> Unit) {
        val docId = debt.id.ifEmpty {
            firestoreService.generateDocumentId(COLLECTION_PATH)
        }

        val debtToSave = if (debt.id.isEmpty()) {
            debt.copy(id = docId)
        } else {
            debt
        }

        firestoreService.addDocument(
            collectionPath = COLLECTION_PATH,
            documentId = docId,
            data = debtToSave,
            onResult = { success -> onResult(success) }
        )
    }

    // Get a single debt by ID
    fun getDebt(debtId: String, onResult: (DebtData?) -> Unit) {
        firestoreService.getDocument(
            collectionPath = COLLECTION_PATH,
            documentId = debtId,
            clazz = DebtData::class.java,
            onResult = onResult
        )
    }

    // Get debts where current user owes someone (fromUserId)
    fun getDebtsOwedByUser(userId: String, onResult: (List<DebtData>) -> Unit) {
        firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "fromUserId",
            value = userId,
            clazz = DebtData::class.java,
            onResult = onResult
        )
    }

    // Get debts where current user is owed money (toUserId)
    fun getDebtsOwedToUser(userId: String, onResult: (List<DebtData>) -> Unit) {
        firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "toUserId",
            value = userId,
            clazz = DebtData::class.java,
            onResult = onResult
        )
    }

    // Get debts for a specific group
    fun getDebtsByGroup(groupId: String, onResult: (List<DebtData>) -> Unit) {
        firestoreService.queryDocuments(
            collectionPath = COLLECTION_PATH,
            field = "groupId",
            value = groupId,
            clazz = DebtData::class.java,
            onResult = onResult
        )
    }

    // Update any field (e.g. status = "settled")
    fun updateDebt(
        debtId: String,
        updates: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        firestoreService.updateDocument(
            collectionPath = COLLECTION_PATH,
            documentId = debtId,
            updates = updates,
            onResult = onResult
        )
    }

    // Delete debt record (optional — you may only allow if cancelled)
    fun deleteDebt(debtId: String, onResult: (Boolean) -> Unit) {
        firestoreService.deleteDocument(
            collectionPath = COLLECTION_PATH,
            documentId = debtId,
            onResult = onResult
        )
    }
}
