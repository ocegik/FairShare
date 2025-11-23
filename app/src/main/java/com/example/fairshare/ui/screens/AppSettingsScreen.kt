package com.example.fairshare.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fairshare.ui.components.LanguageManager
import com.example.fairshare.ui.components.SubHeader
import com.example.fairshare.ui.theme.AppTheme
import com.example.fairshare.viewmodel.ThemeViewModel

@Composable
fun AppSettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    // You might want to collect the current theme state to highlight the active button
    // val currentTheme by themeViewModel.theme.collectAsState()

    Scaffold(
        topBar = {
            SubHeader(title = "About", onBack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Language Section ---
            SettingsSectionTitle("Language")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {
                    LanguageManager.setAppLanguage(context, "en")
                    (context as? Activity)?.recreate()
                }) { Text("English") }

                OutlinedButton(onClick = {
                    LanguageManager.setAppLanguage(context, "hi")
                    (context as? Activity)?.recreate()
                }) { Text("हिंदी") }
            }

            HorizontalDivider()

            // --- Theme Section ---
            SettingsSectionTitle("Appearance")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // You can make these RadioButtons or selectable rows for better UI
                Button(
                    onClick = { themeViewModel.setTheme(AppTheme.LIGHT) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) { Text("Light Mode") }

                Button(
                    onClick = { themeViewModel.setTheme(AppTheme.DARK) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text("Dark Mode") }

                Button(
                    onClick = { themeViewModel.setTheme(AppTheme.BRAND) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
                ) { Text("Pink Brand") }
            }
        }
    }
}