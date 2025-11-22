package com.example.fairshare

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read preferences ONCE before UI
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val onboardingDone = prefs.getBoolean("onboarding_done", false)
        val profileSetupDone = prefs.getBoolean("profile_setup_done", false)

        setContent {
            MyApp(
                onboardingDone = onboardingDone,
                profileSetupDone = profileSetupDone
            )
        }
    }
}

