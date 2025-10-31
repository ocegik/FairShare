package com.example.fairshare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.DebtData
import com.example.fairshare.data.models.ExpenseData
import com.example.fairshare.data.models.UserPreferences
import com.example.fairshare.data.models.UserStats
import com.example.fairshare.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val appContext: Application
) : ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
        private const val STATS_SUBCOLLECTION = "stats"
        private const val STATS_DOCUMENT_ID = "main"
    }

    private val userPrefs = UserPreferences(appContext)

    private val _userProfile = MutableStateFlow<Map<String, Any>?>(null)
    val displayName = _userProfile.map { it?.get("displayName") as? String ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val email = _userProfile.map { it?.get("email") as? String ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val photoUrl = _userProfile.map { it?.get("photoUrl") as? String ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats

    init {
        loadUserFromCache()
        refreshUserFromFirestore()
        fetchUserStats()
    }
    private fun loadUserFromCache() {
        viewModelScope.launch {
            userPrefs.userData.firstOrNull()?.let { localData ->
                if (localData["displayName"].isNullOrEmpty()) return@let
                _userProfile.value = localData
            }

            // Keep collecting for changes (optional)
            userPrefs.userData.collect { localData ->
                if (localData["displayName"].isNullOrEmpty()) return@collect
                _userProfile.value = localData
            }
        }
    }

    fun refreshUserFromFirestore() {
        val uid = auth.currentUser?.uid ?: return
        userRepository.getUser(uid) { data ->
            if (data != null) {
                _userProfile.value = data
                viewModelScope.launch {
                    userPrefs.saveUser(
                        displayName = data["displayName"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        photoUrl = data["photoUrl"] as? String ?: ""
                    )
                }
            }
        }
    }

    fun clearUser() {
        viewModelScope.launch { userPrefs.clearUser() }
        _userProfile.value = null
        _userStats.value = null
    }


    // Load current user's profile from Firestore
    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot load user - UID is NULL")
            return
        }
        _isLoading.value = true
        userRepository.getUser(uid) { data ->
            _userProfile.value = data
            _isLoading.value = false
        }
    }


    // Update user profile (bio, displayName, etc.)
    fun updateProfile(updates: Map<String, Any>, onComplete: (Boolean) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot update profile - UID is NULL")
            onComplete(false)
            return
        }

        _isLoading.value = true
        userRepository.updateUser(uid, updates) { success ->
            _isLoading.value = false
            if (success) {
                loadCurrentUser()
                Log.d(TAG, "Profile updated successfully")
            } else {
                Log.e(TAG, "Failed to update profile")
            }
            onComplete(success)
        }
    }

    // Update specific fields
    fun updateDisplayName(newName: String, onComplete: (Boolean) -> Unit = {}) {
        updateProfile(mapOf("displayName" to newName), onComplete)
    }

    fun updateBio(newBio: String, onComplete: (Boolean) -> Unit = {}) {
        updateProfile(mapOf("bio" to newBio), onComplete)
    }

    fun updatePhotoUrl(newPhotoUrl: String, onComplete: (Boolean) -> Unit = {}) {
        updateProfile(mapOf("photoUrl" to newPhotoUrl), onComplete)
    }

    // Delete user account (removes Firestore data)
    fun deleteUserAccount(onComplete: (Boolean) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot delete account - UID is NULL")
            onComplete(false)
            return
        }

        _isLoading.value = true
        userRepository.deleteUser(uid) { success ->
            _isLoading.value = false
            if (success) {
                clearUser()
                Log.d(TAG, "User account deleted successfully")
            } else {
                Log.e(TAG, "Failed to delete user account")
            }
            onComplete(success)
        }
    }

    fun fetchUserStats() {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot fetch stats - UID is NULL")
            return
        }

        _isLoading.value = true
        userRepository.getUserSubCollectionDocument(
            userId = uid,
            subCollectionName = STATS_SUBCOLLECTION,
            documentId = STATS_DOCUMENT_ID,
            clazz = UserStats::class.java
        ) { stats ->
            _isLoading.value = false

            if (stats != null) {
                _userStats.value = stats
                Log.d(TAG, "✅ Stats loaded: $stats")
            } else {
                // Initialize with default stats if none exist
                _userStats.value = UserStats()
                initializeUserStats()
                Log.w(TAG, "⚠️ No stats document found, initializing defaults")
            }
        }
    }
    private fun initializeUserStats() {
        val uid = auth.currentUser?.uid ?: return

        userRepository.addToUserSubCollection(
            userId = uid,
            subCollectionName = STATS_SUBCOLLECTION,
            documentId = STATS_DOCUMENT_ID,
            data = UserStats()
        ) { success ->
            if (success) {
                Log.d(TAG, "✅ Stats document initialized")
            } else {
                Log.e(TAG, "❌ Failed to initialize stats")
            }
        }
    }
    fun updateStatsForExpense(expense: ExpenseData, isAdding: Boolean = true) {
        val currentStats = _userStats.value ?: UserStats()
        val multiplier = if (isAdding) 1.0 else -1.0
        val amount = expense.amount * multiplier

        val updatedStats = when (expense.entryType.lowercase()) {
            "expense" -> currentStats.copy(
                expense = (currentStats.expense + amount).coerceAtLeast(0.0)
            )
            "income" -> currentStats.copy(
                income = (currentStats.income + amount).coerceAtLeast(0.0)
            )
            else -> {
                Log.w(TAG, "Unknown entry type: ${expense.entryType}")
                return
            }
        }

        saveUserStats(updatedStats)
    }

    fun updateStatsForDebt(debt: DebtData, operation: DebtOperation) {
        val uid = auth.currentUser?.uid ?: return
        val currentStats = _userStats.value ?: UserStats()

        val updatedStats = when (operation) {
            // When user owes money (debt added)
            DebtOperation.DEBT_ADDED -> {
                if (debt.fromUserId == uid) {
                    currentStats.copy(
                        debt = (currentStats.debt + debt.amount).coerceAtLeast(0.0)
                    )
                } else if (debt.toUserId == uid) {
                    currentStats.copy(
                        receivables = (currentStats.receivables + debt.amount).coerceAtLeast(0.0)
                    )
                } else currentStats
            }
            // When debt is settled
            DebtOperation.DEBT_SETTLED -> {
                if (debt.fromUserId == uid) {
                    currentStats.copy(
                        debt = (currentStats.debt - debt.amount).coerceAtLeast(0.0)
                    )
                } else if (debt.toUserId == uid) {
                    currentStats.copy(
                        receivables = (currentStats.receivables - debt.amount).coerceAtLeast(0.0)
                    )
                } else currentStats
            }
            // When debt is cancelled/deleted
            DebtOperation.DEBT_CANCELLED -> {
                if (debt.fromUserId == uid) {
                    currentStats.copy(
                        debt = (currentStats.debt - debt.amount).coerceAtLeast(0.0)
                    )
                } else if (debt.toUserId == uid) {
                    currentStats.copy(
                        receivables = (currentStats.receivables - debt.amount).coerceAtLeast(0.0)
                    )
                } else currentStats
            }
        }

        if (updatedStats != currentStats) {
            saveUserStats(updatedStats)
        }
    }

    private fun saveUserStats(stats: UserStats) {
        val uid = auth.currentUser?.uid ?: return

        userRepository.addToUserSubCollection(
            userId = uid,
            subCollectionName = STATS_SUBCOLLECTION,
            documentId = STATS_DOCUMENT_ID,
            data = stats
        ) { success ->
            if (success) {
                _userStats.value = stats
                Log.d(TAG, "✅ Stats updated: $stats")
            } else {
                Log.e(TAG, "❌ Failed to update stats")
            }
        }
    }

    fun refreshStats() {
        fetchUserStats()
    }

}

enum class DebtOperation {
    DEBT_ADDED,      // When a new debt is created
    DEBT_SETTLED,    // When a debt is marked as settled
    DEBT_CANCELLED   // When a debt is cancelled/deleted
}
