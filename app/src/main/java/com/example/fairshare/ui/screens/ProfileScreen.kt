package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fairshare.core.data.models.SettingsCategory
import com.example.fairshare.core.ui.BackButton
import com.example.fairshare.navigation.Screen
import com.example.fairshare.ui.components.SettingsCategoryItem
import com.example.fairshare.viewmodel.UserViewModel


@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    navController: NavHostController
) {
    val displayName by userViewModel.displayName.collectAsState()
    val photoUrl by userViewModel.photoUrl.collectAsState()
    val email by userViewModel.email.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        // ---------- TOP BAR ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = { navController.popBackStack() })
            Spacer(Modifier.width(12.dp))
            Text(
                "Profile",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(24.dp))

        // ---------- HEADER ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text("Hello, $displayName", style = MaterialTheme.typography.titleMedium)
                Text(email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ---------- SETTINGS LIST ----------
        val categories = listOf(
            SettingsCategory(
                title = "Account",
                description = "Privacy, security, sign out",
                onClick = { navController.navigate(Screen.AccountSettings.route) }
            ),
            SettingsCategory(
                title = "App Settings",
                description = "Theme, language, preferences",
                onClick = { navController.navigate(Screen.AppSettings.route) }
            ),
            SettingsCategory(
                title = "About",
                description = "App version and info",
                onClick = { navController.navigate(Screen.AboutApp.route) }
            )
        )

        categories.forEachIndexed { index, category ->
            SettingsCategoryItem(category)
            if (index < categories.size - 1) {
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            }
        }
    }
}

