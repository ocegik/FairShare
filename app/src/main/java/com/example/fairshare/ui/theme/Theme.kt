package com.example.fairshare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// -------------------------------
//  THEME 1 — DEEP BLUE + TEAL
// -------------------------------

val DarkColorScheme_1 = darkColorScheme(
    primary = Color(0xFF1A73E8),
    primaryContainer = Color(0xFF174EA6),
    secondary = Color(0xFF00B8A9),
    secondaryContainer = Color(0xFF003E39),

    background = Color(0xFF0F0F0F),
    surface = Color(0xFF1A1A1A),

    onPrimary = Color.White,
    onBackground = Color(0xFFE6E6E6),
    onSurface = Color(0xFFD0D0D0)
)

val LightColorScheme_1 = lightColorScheme(
    primary = Color(0xFF1A73E8),
    primaryContainer = Color(0xFFD2E3FC),
    secondary = Color(0xFF00B8A9),
    secondaryContainer = Color(0xFFCCF4EF),

    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF8F8F8),

    onPrimary = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF333333)
)


// -------------------------------
//  THEME 2 — INDIGO + CYAN
// -------------------------------

val DarkColorScheme_2 = darkColorScheme(
    primary = Color(0xFF3F51B5),
    primaryContainer = Color(0xFF303F9F),
    secondary = Color(0xFF00BCD4),
    secondaryContainer = Color(0xFF004F56),

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),

    onPrimary = Color.White,
    onBackground = Color(0xFFEAEAEA),
    onSurface = Color(0xFFCCCCCC)
)

val LightColorScheme_2 = lightColorScheme(
    primary = Color(0xFF3F51B5),
    primaryContainer = Color(0xFFD1D9FF),
    secondary = Color(0xFF00BCD4),
    secondaryContainer = Color(0xFFCCF7FF),

    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F7F7),

    onPrimary = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF333333)
)


// -------------------------------
//  THEME 3 — EMERALD + GRAPHITE
// -------------------------------

val DarkColorScheme_3 = darkColorScheme(
    primary = Color(0xFF2E7D32),
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF26A69A),
    secondaryContainer = Color(0xFF004D40),

    background = Color(0xFF0E0E0E),
    surface = Color(0xFF1A1A1A),

    onPrimary = Color.White,
    onBackground = Color(0xFFE6E6E6),
    onSurface = Color(0xFFCCCCCC)
)

val LightColorScheme_3 = lightColorScheme(
    primary = Color(0xFF2E7D32),
    primaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFF26A69A),
    secondaryContainer = Color(0xFFC9F3EE),

    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF8F8F8),

    onPrimary = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF333333)
)


// -------------------------------------
//  ENUM TO SWITCH ANY PALETTE YOU WANT
// -------------------------------------

enum class AppTheme {
    LIGHT_1, DARK_1,
    LIGHT_2, DARK_2,
    LIGHT_3, DARK_3
}


// -------------------------------
//  MAIN THEME WRAPPER
// -------------------------------

@Composable
fun FairShareTheme(
    currentTheme: AppTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when (currentTheme) {

        // Blue + Teal
        AppTheme.LIGHT_1 -> LightColorScheme_1
        AppTheme.DARK_1 -> DarkColorScheme_1

        // Indigo + Cyan
        AppTheme.LIGHT_2 -> LightColorScheme_2
        AppTheme.DARK_2 -> DarkColorScheme_2

        // Emerald + Graphite
        AppTheme.LIGHT_3 -> LightColorScheme_3
        AppTheme.DARK_3 -> DarkColorScheme_3
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
