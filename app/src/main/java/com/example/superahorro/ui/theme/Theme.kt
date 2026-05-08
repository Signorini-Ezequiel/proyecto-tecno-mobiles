package com.example.superahorro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
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

private val LightColorScheme = lightColorScheme(
    primary = SuperAhorroGreenDark,
    onPrimary = SuperAhorroLightOnPrimary,
    primaryContainer = SuperAhorroLightPrimaryContainer,
    onPrimaryContainer = SuperAhorroLightTextPrimary,
    secondary = SuperAhorroGreen,
    onSecondary = SuperAhorroLightOnPrimary,
    tertiary = SuperAhorroGreenSoft,
    background = SuperAhorroLightBackground,
    onBackground = SuperAhorroLightTextPrimary,
    surface = SuperAhorroLightSurface,
    onSurface = SuperAhorroLightTextPrimary,
    surfaceVariant = SuperAhorroLightSurfaceVariant,
    onSurfaceVariant = SuperAhorroLightTextSecondary,
    outline = SuperAhorroLightOutline,
    error = SuperAhorroLightError
)

@Composable
fun SuperAhorroTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
