package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1E1303),
    primaryContainer = Color(0xFF3D2A08),
    onPrimaryContainer = GoldLight,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF281C06),
    onSecondaryContainer = GoldLight,
    tertiary = SteelBlue,
    onTertiary = Color.Black,
    background = PetroleumDarkBg,
    onBackground = TextPrimary,
    surface = PetroleumNavy,
    onSurface = TextPrimary,
    surfaceVariant = SteelDark,
    onSurfaceVariant = TextSecondary,
    outline = PetroleumCardBorder,
    error = CrimsonAlert
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDF0D5),
    onPrimaryContainer = Color(0xFF4A3408),
    secondary = AmberAccent,
    onSecondary = Color.Black,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    error = CrimsonAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Default to dark theme for authentic military oil brigade aesthetic
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
