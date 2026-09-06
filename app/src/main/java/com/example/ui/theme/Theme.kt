package com.example.ui.theme

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
    primary = KarigorEmeraldLight,
    onPrimary = KarigorNavy,
    primaryContainer = KarigorEmeraldDark,
    onPrimaryContainer = KarigorEmeraldContainer,
    secondary = KarigorGold,
    onSecondary = Color.Black,
    secondaryContainer = KarigorGoldDark,
    onSecondaryContainer = KarigorGoldContainer,
    background = Color(0xFF0B1320),
    surface = Color(0xFF131D2E),
    surfaceVariant = Color(0xFF1E293B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC),
    error = KarigorError
)

private val LightColorScheme = lightColorScheme(
    primary = KarigorEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = KarigorEmeraldContainer,
    onPrimaryContainer = KarigorEmeraldOnContainer,
    secondary = KarigorGold,
    onSecondary = Color.Black,
    secondaryContainer = KarigorGoldContainer,
    onSecondaryContainer = KarigorGoldDark,
    background = KarigorBackgroundLight,
    surface = KarigorSurfaceLight,
    surfaceVariant = KarigorSurfaceVariant,
    onBackground = KarigorNavy,
    onSurface = KarigorSlateDark,
    error = KarigorError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set default to false to showcase Karigor's distinct brand palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
