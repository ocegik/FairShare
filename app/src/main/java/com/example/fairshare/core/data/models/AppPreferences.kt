package com.example.fairshare.core.data.models

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val context: Context) {

    private val onBoardingComplete = booleanPreferencesKey("onboarding_done")
    private val profileSetupComplete = booleanPreferencesKey("profile_setup_done")

    val onboardingDone: Flow<Boolean> = context.userDataStore.data
        .map { it[onBoardingComplete] ?: false }

    val profileSetupDone: Flow<Boolean> = context.userDataStore.data
        .map { it[profileSetupComplete] ?: false }

    suspend fun setOnboardingDone() {
        context.userDataStore.edit { it[onBoardingComplete] = true }
    }

    suspend fun setProfileSetupDone() {
        context.userDataStore.edit { it[profileSetupComplete] = true }
    }
}