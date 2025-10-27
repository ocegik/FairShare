package com.example.fairshare.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "FirestoreService"
    }

    // Generic CRUD operations
    fun <T : Any> addDocument(
        collectionPath: String,
        documentId: String,
        data: T,
        onResult: (Boolean) -> Unit
    ) {
        Log.d(TAG, "=== FIRESTORE WRITE ATTEMPT ===")
        Log.d(TAG, "Collection: $collectionPath")
        Log.d(TAG, "Document ID: $documentId")
        Log.d(TAG, "Data: $data")
        Log.d(TAG, "Data class: ${data::class.java.name}")
        Log.d(TAG, "Current user: ${auth.currentUser?.uid}")
        Log.d(TAG, "User authenticated: ${auth.currentUser != null}")

        firestore.collection(collectionPath)
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "✅ SUCCESS: Document written to Firestore")
                onResult(true) }
            .addOnFailureListener { exception ->
                Log.e(TAG, "❌ FAILURE: Write failed")
                Log.e(TAG, "Error type: ${exception::class.java.simpleName}")
                Log.e(TAG, "Error message: ${exception.message}")
                Log.e(TAG, "Error cause: ${exception.cause}")
                exception.printStackTrace()
                onResult(false) }
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

    fun generateDocumentId(collectionPath: String): String {
        return firestore.collection(collectionPath).document().id
    }
}
