package com.example.fairshare.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    // Generic CRUD operations
    suspend fun <T : Any> addDocument(
        collectionPath: String,
        documentId: String,
        data: T
    ): Result<Unit> {  // ← Returns Result with success OR error details
        return try {
            firestore.collection(collectionPath)
                .document(documentId)
                .set(data)
                .await()  // ← Suspends until complete (no callback needed!)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)  // ← Keeps actual error message
        }
    }

    suspend fun <T> getDocument(
        collectionPath: String,
        documentId: String,
        clazz: Class<T>
    ): Result<T> {
        return try {
            val snapshot = firestore.collection(collectionPath)
                .document(documentId)
                .get()
                .await()

            val data = snapshot.toObject(clazz)
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Document not found or data is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)  // ← Network error is distinct
        }
    }

    suspend fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(collectionPath)
                .document(documentId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDocument(
        collectionPath: String,
        documentId: String
    ): Result<Unit> {
        return try {
            firestore.collection(collectionPath)
                .document(documentId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun <T> getAllDocuments(
        collectionPath: String,
        clazz: Class<T>
    ): Result<List<T>> {
        return try {
            val query = firestore.collection(collectionPath)
                .get()
                .await()

            val list = query.documents.mapNotNull { it.toObject(clazz) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)  // ← Error is distinct from empty result
        }
    }

    suspend fun getDocumentAsMap(
        collectionPath: String,
        documentId: String
    ): Result<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection(collectionPath)
                .document(documentId)
                .get()
                .await()

            if (snapshot.exists() && snapshot.data != null) {
                Result.success(snapshot.data!!)
            } else {
                Result.failure(Exception("Document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllDocumentsAsMap(
        collectionPath: String
    ): Result<List<Map<String, Any>>> {
        return try {
            val query = firestore.collection(collectionPath)
                .get()
                .await()

            val list = query.documents.mapNotNull { it.data }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun <T> queryDocuments(
        collectionPath: String,
        field: String,
        value: Any,
        clazz: Class<T>
    ): Result<List<T>> {
        return try {
            val query = firestore.collection(collectionPath)
                .whereEqualTo(field, value)
                .get()
                .await()

            val list = query.documents.mapNotNull { it.toObject(clazz) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateDocumentId(collectionPath: String): String {
        return firestore.collection(collectionPath).document().id
    }
}
