package com.example.fairshare.data.models

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_PREFS_NAME = "user_preferences"

val Context.userDataStore by preferencesDataStore(
    name = USER_PREFS_NAME
)

class UserPreferences(private val context: Context) {

    companion object {
        private val DISPLAY_NAME = stringPreferencesKey("display_name")
        private val EMAIL = stringPreferencesKey("email")
        private val PHOTO_URL = stringPreferencesKey("photo_url")
        private val BOOKMARK_GROUP = stringPreferencesKey("bookmarked_group")
    }

    // --- Save user data ---
    suspend fun saveUser(displayName: String, email: String, photoUrl: String) {
        context.userDataStore.edit { prefs ->
            prefs[DISPLAY_NAME] = displayName
            prefs[EMAIL] = email
            prefs[PHOTO_URL] = photoUrl
        }
    }

    // --- Read user data ---
    val userData: Flow<Map<String, String>> = context.userDataStore.data.map { prefs ->
        mapOf(
            "displayName" to (prefs[DISPLAY_NAME] ?: ""),
            "email" to (prefs[EMAIL] ?: ""),
            "photoUrl" to (prefs[PHOTO_URL] ?: ""),
            "bookMarkedGroup" to (prefs[BOOKMARK_GROUP] ?: "")
        )
    }

    // --- Clear user data (on logout) ---
    suspend fun clearUser() {
        context.userDataStore.edit { it.clear() }
    }
}
