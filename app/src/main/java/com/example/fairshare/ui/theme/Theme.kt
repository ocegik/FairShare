package com.example.fairshare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD72669),
    secondary = Color(0xFFF24986),
    background = Color(0xFF0E0E0E),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.White,
)


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD72669),
    secondary = Color(0xFFF5A9C5),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF8F8F8),
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1A1A),
)

private val BrandColorScheme = darkColorScheme(
    primary = Color(0xFFD72669),
    secondary = Color(0xFFF24986),
    background = Color(0xFF121212),
    surface = Color(0xFF1A1A1A),
)



@Composable
fun FairShareTheme(
    currentTheme: AppTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when (currentTheme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.BRAND -> BrandColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


enum class AppTheme {
    LIGHT,
    DARK,
    BRAND // your custom pink-based theme
}
