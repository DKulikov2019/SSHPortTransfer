package com.dkulikov2019.sshporttransfer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.dkulikov2019.sshporttransfer.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryTeal,
    surface = SurfaceLight,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryTeal,
    error = ErrorRed
)

@Composable
fun SSHPortTransferTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
