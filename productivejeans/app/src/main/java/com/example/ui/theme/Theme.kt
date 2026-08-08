package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PhoningAccentHotPink,
    onPrimary = Color.White,
    primaryContainer = PhoningPink,
    onPrimaryContainer = PhoningNavyText,
    secondary = PhoningBlueDark,
    onSecondary = Color.White,
    secondaryContainer = PhoningBlue,
    onSecondaryContainer = PhoningNavyText,
    tertiary = PhoningYellow,
    onTertiary = PhoningNavyText,
    tertiaryContainer = PhoningYellow,
    onTertiaryContainer = PhoningNavyText,
    background = PhoningBackground,
    onBackground = PhoningNavyText,
    surface = PhoningCardBg,
    onSurface = PhoningNavyText,
    surfaceVariant = PhoningLilac.copy(alpha = 0.4f),
    onSurfaceVariant = PhoningNavyText
)

private val DarkColorScheme = darkColorScheme(
    primary = PhoningPink,
    onPrimary = PhoningNavyText,
    primaryContainer = PhoningPinkDark,
    onPrimaryContainer = Color.White,
    secondary = PhoningBlue,
    onSecondary = PhoningNavyText,
    secondaryContainer = PhoningBlueDark,
    onSecondaryContainer = Color.White,
    tertiary = PhoningYellow,
    onTertiary = PhoningNavyText,
    background = Color(0xFF14142B),
    onBackground = Color(0xFFF0F0FF),
    surface = Color(0xFF1F1F3D),
    onSurface = Color(0xFFF0F0FF),
    surfaceVariant = Color(0xFF2D2D54),
    onSurfaceVariant = Color(0xFFD0D0FF)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Phoning branded colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

