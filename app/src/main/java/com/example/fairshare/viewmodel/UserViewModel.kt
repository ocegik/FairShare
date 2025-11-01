package com.example.fairshare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fairshare.data.models.DebtData
import com.example.fairshare.data.models.ExpenseData
import com.example.fairshare.data.models.User
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
import kotlin.collections.emptyList

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

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile
    val displayName: StateFlow<String> = _userProfile.map { it?.displayName ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val email: StateFlow<String> = _userProfile.map { it?.email ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val photoUrl: StateFlow<String> = _userProfile.map { it?.photoUrl ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userGroups: StateFlow<List<String>> = _userProfile.map { it?.groups ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


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

                // Convert cached map to User object
                val cachedUser = User(
                    auth.currentUser?.uid ?: "",
                    localData["displayName"],
                    localData["email"],
                    localData["photoUrl"],
                    emptyList()
                )
                _userProfile.value = cachedUser
            }

            // Keep collecting for changes
            userPrefs.userData.collect { localData ->
                if (localData["displayName"].isNullOrEmpty()) return@collect

                val cachedUser = User(
                    uid = auth.currentUser?.uid ?: "",
                    displayName = localData["displayName"],
                    email = localData["email"],
                    photoUrl = localData["photoUrl"],
                    groups = _userProfile.value?.groups ?: emptyList()
                )
                _userProfile.value = cachedUser
            }
        }
    }

    fun refreshUserFromFirestore() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            userRepository.getUser(uid)
                .onSuccess { user ->
                    _userProfile.value = user
                    userPrefs.saveUser(
                        displayName = user.displayName ?: "",
                        email = user.email ?: "",
                        photoUrl = user.photoUrl ?: ""
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to refresh user from Firestore", exception)
                }
        }
    }

    fun clearUser() {
        viewModelScope.launch {
            userPrefs.clearUser()
        }
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
        viewModelScope.launch {
            userRepository.getUser(uid)
                .onSuccess { user ->
                    _userProfile.value = user
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load user", exception)
                }
            _isLoading.value = false
        }
    }


    // Update user profile (bio, displayName, etc.)
    fun updateProfile(updatedUser: User, onComplete: (Boolean) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot update profile - UID is NULL")
            onComplete(false)
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val userData = mapOf(
                "displayName" to (updatedUser.displayName ?: ""),
                "email" to (updatedUser.email ?: ""),
                "photoUrl" to (updatedUser.photoUrl ?: ""),
                "groups" to updatedUser.groups
            )

            userRepository.updateUser(uid, userData)
                .onSuccess {
                    loadCurrentUser()
                    Log.d(TAG, "Profile updated successfully")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to update profile", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    // Update specific fields
    fun updateDisplayName(newName: String, onComplete: (Boolean) -> Unit = {}) {
        val currentUser = _userProfile.value ?: run {
            Log.e(TAG, "Cannot update display name - user profile is null")
            onComplete(false)
            return
        }
        updateProfile(currentUser.copy(displayName = newName), onComplete)
    }

    fun updatePhotoUrl(newPhotoUrl: String, onComplete: (Boolean) -> Unit = {}) {
        val currentUser = _userProfile.value ?: run {
            Log.e(TAG, "Cannot update photo URL - user profile is null")
            onComplete(false)
            return
        }
        updateProfile(currentUser.copy(photoUrl = newPhotoUrl), onComplete)
    }

    // Delete user account (removes Firestore data)
    fun deleteUserAccount(onComplete: (Boolean) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot delete account - UID is NULL")
            onComplete(false)
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            userRepository.deleteUser(uid)
                .onSuccess {
                    clearUser()
                    Log.d(TAG, "User account deleted successfully")
                    onComplete(true)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to delete user account", exception)
                    onComplete(false)
                }
            _isLoading.value = false
        }
    }

    fun fetchUserStats() {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot fetch stats - UID is NULL")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            userRepository.getUserSubCollectionDocument(
                userId = uid,
                subCollectionName = STATS_SUBCOLLECTION,
                documentId = STATS_DOCUMENT_ID,
                clazz = UserStats::class.java
            )
                .onSuccess { stats ->
                    _userStats.value = stats
                    Log.d(TAG, "✅ Stats loaded: $stats")
                }
                .onFailure { exception ->
                    // Initialize with default stats if none exist
                    _userStats.value = UserStats()
                    initializeUserStats()
                    Log.w(TAG, "⚠️ No stats document found, initializing defaults", exception)
                }
            _isLoading.value = false
        }
    }

    private fun initializeUserStats() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            userRepository.addToUserSubCollection(
                userId = uid,
                subCollectionName = STATS_SUBCOLLECTION,
                documentId = STATS_DOCUMENT_ID,
                data = UserStats()
            )
                .onSuccess {
                    Log.d(TAG, "✅ Stats document initialized")
                }
                .onFailure { exception ->
                    Log.e(TAG, "❌ Failed to initialize stats", exception)
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

        viewModelScope.launch {
            userRepository.addToUserSubCollection(
                userId = uid,
                subCollectionName = STATS_SUBCOLLECTION,
                documentId = STATS_DOCUMENT_ID,
                data = stats
            )
                .onSuccess {
                    _userStats.value = stats
                    Log.d(TAG, "✅ Stats updated: $stats")
                }
                .onFailure { exception ->
                    Log.e(TAG, "❌ Failed to update stats", exception)
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
