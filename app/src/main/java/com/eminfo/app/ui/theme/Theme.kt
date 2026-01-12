package com.eminfo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmergencyGreen,
    onPrimary = Color.White,
    primaryContainer = EmergencyGreenLight,
    secondary = EmergencyBlue,
    background = SurfaceLight,
    surface = CardBackground,
    onSurface = TextPrimary,
    error = EmergencyRed
)

@Composable
fun EmergencyInfoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}