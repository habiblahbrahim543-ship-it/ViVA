package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VivaPink,
    onPrimary = Color.White,
    primaryContainer = VivaSurfaceElevated,
    onPrimaryContainer = Color.White,
    secondary = VivaCyan,
    onSecondary = Color.Black,
    secondaryContainer = VivaSurfaceVariant,
    onSecondaryContainer = VivaCyan,
    tertiary = VivaPurple,
    onTertiary = Color.White,
    background = VivaBlack,
    onBackground = VivaTextPrimary,
    surface = VivaDarkBg,
    onSurface = VivaTextPrimary,
    surfaceVariant = VivaSurfaceDark,
    onSurfaceVariant = VivaTextSecondary,
    outline = VivaBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // VIVA is designed as a dark-mode first video experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
