package com.example.fairshare.ui.components

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LanguageManager {

    fun setAppLanguage(context: Context, languageTag: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)
        } else {
            // For Android < 13
            val resources = context.resources
            val config = resources.configuration
            config.setLocale(Locale(languageTag))
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}
