package com.example.superahorro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SuperAhorroGreen,
    onPrimary = SuperAhorroDarkBackground,
    primaryContainer = SuperAhorroGreenDark,
    onPrimaryContainer = SuperAhorroTextPrimary,
    secondary = SuperAhorroGreenSoft,
    onSecondary = SuperAhorroDarkBackground,
    tertiary = SuperAhorroGreenSoft,
    background = SuperAhorroDarkBackground,
    onBackground = SuperAhorroTextPrimary,
    surface = SuperAhorroDarkSurface,
    onSurface = SuperAhorroTextPrimary,
    surfaceVariant = SuperAhorroCardGray,
    onSurfaceVariant = SuperAhorroTextSecondary,
    outline = SuperAhorroOutline,
    error = SuperAhorroError
)

@Composable
fun SuperAhorroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
