package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SittuvaRedGlow,
    onPrimary = Color.White,
    primaryContainer = SittuvaCrimson,
    onPrimaryContainer = Color.White,
    secondary = SittuvaGold,
    onSecondary = Color.Black,
    secondaryContainer = SittuvaGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = SittuvaRedAccent,
    background = SittuvaDarkBg,
    onBackground = Color(0xFFFCE4EC),
    surface = SittuvaDarkSurface,
    onSurface = Color(0xFFFCE4EC),
    surfaceVariant = SittuvaDarkCard,
    onSurfaceVariant = Color(0xFFE0C4C9),
    outline = SittuvaDarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = SittuvaCrimson,
    onPrimary = Color.White,
    primaryContainer = SittuvaCrimsonLight,
    onPrimaryContainer = Color.White,
    secondary = SittuvaGoldDark,
    onSecondary = Color.White,
    secondaryContainer = SittuvaGoldLight,
    onSecondaryContainer = Color.Black,
    tertiary = SittuvaRedGlow,
    background = SittuvaLightBg,
    onBackground = Color(0xFF200B0E),
    surface = SittuvaLightSurface,
    onSurface = Color(0xFF200B0E),
    surfaceVariant = SittuvaLightCard,
    onSurfaceVariant = Color(0xFF5C262C),
    outline = SittuvaLightBorder
)

@Composable
fun SittuvaTheme(
    darkTheme: Boolean = true, // Default to stunning luminous dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SittuvaTheme(darkTheme = darkTheme, content = content)
}
