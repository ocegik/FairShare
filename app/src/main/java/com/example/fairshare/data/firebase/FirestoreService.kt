package com.example.fairshare.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    // Generic CRUD operations
    fun <T : Any> addDocument(
        collectionPath: String,  // Can be "users" OR "users/$uid/expenses"
        documentId: String,
        data: T,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection(collectionPath)
            .document(documentId)
            .set(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun <T> getDocument(
        collectionPath: String,
        documentId: String,
        clazz: Class<T>,
        onResult: (T?) -> Unit
    ) {
        firestore.collection(collectionPath)
            .document(documentId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.toObject(clazz))
            }
            .addOnFailureListener { onResult(null) }
    }

    fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection(collectionPath)
            .document(documentId)
            .update(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteDocument(
        collectionPath: String,
        documentId: String,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection(collectionPath)
            .document(documentId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun <T> getAllDocuments(
        collectionPath: String,
        clazz: Class<T>,
        onResult: (List<T>) -> Unit
    ) {
        firestore.collection(collectionPath)
            .get()
            .addOnSuccessListener { query ->
                val list = query.documents.mapNotNull { it.toObject(clazz) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getDocumentAsMap(
        collectionPath: String,
        documentId: String,
        onResult: (Map<String, Any>?) -> Unit
    ) {
        firestore.collection(collectionPath)
            .document(documentId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) onResult(snapshot.data)
                else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getAllDocumentsAsMap(
        collectionPath: String,
        onResult: (List<Map<String, Any>>) -> Unit
    ) {
        firestore.collection(collectionPath)
            .get()
            .addOnSuccessListener { query ->
                val list = query.documents.mapNotNull { it.data }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun <T> queryDocuments(
        collectionPath: String,
        field: String,
        value: Any,
        clazz: Class<T>,
        onResult: (List<T>) -> Unit
    ) {
        firestore.collection(collectionPath)
            .whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { query ->
                val list = query.documents.mapNotNull { it.toObject(clazz) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun generateDocumentId(collectionPath: String): String {
        return firestore.collection(collectionPath).document().id
    }
}
