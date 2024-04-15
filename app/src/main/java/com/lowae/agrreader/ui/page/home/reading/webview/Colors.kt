package com.lowae.agrreader.ui.page.home.reading.webview

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb

class HexColorScheme(
    val primary: String,
    val onPrimary: String,
    val primaryContainer: String,
    val onPrimaryContainer: String,
    val inversePrimary: String,
    val secondary: String,
    val onSecondary: String,
    val secondaryContainer: String,
    val onSecondaryContainer: String,
    val tertiary: String,
    val onTertiary: String,
    val tertiaryContainer: String,
    val onTertiaryContainer: String,
    val background: String,
    val onBackground: String,
    val surface: String,
    val onSurface: String,
    val surfaceVariant: String,
    val onSurfaceVariant: String,
    val surfaceTint: String,
    val inverseSurface: String,
    val inverseOnSurface: String,
    val error: String,
    val onError: String,
    val errorContainer: String,
    val onErrorContainer: String,
    val outline: String,
    val outlineVariant: String,
    val scrim: String,
) {
    companion object {
        fun fromColorScheme(colorScheme: ColorScheme): HexColorScheme {
            return HexColorScheme(
                argbToCssColor(colorScheme.primary.toArgb()),
                argbToCssColor(colorScheme.onPrimary.toArgb()),
                argbToCssColor(colorScheme.primaryContainer.toArgb()),
                argbToCssColor(colorScheme.onPrimaryContainer.toArgb()),
                argbToCssColor(colorScheme.inversePrimary.toArgb()),
                argbToCssColor(colorScheme.secondary.toArgb()),
                argbToCssColor(colorScheme.onSecondary.toArgb()),
                argbToCssColor(colorScheme.secondaryContainer.toArgb()),
                argbToCssColor(colorScheme.onSecondaryContainer.toArgb()),
                argbToCssColor(colorScheme.tertiary.toArgb()),
                argbToCssColor(colorScheme.onTertiary.toArgb()),
                argbToCssColor(colorScheme.tertiaryContainer.toArgb()),
                argbToCssColor(colorScheme.onTertiaryContainer.toArgb()),
                argbToCssColor(colorScheme.background.toArgb()),
                argbToCssColor(colorScheme.onBackground.toArgb()),
                argbToCssColor(colorScheme.surface.toArgb()),
                argbToCssColor(colorScheme.onSurface.toArgb()),
                argbToCssColor(colorScheme.surfaceVariant.toArgb()),
                argbToCssColor(colorScheme.onSurfaceVariant.toArgb()),
                argbToCssColor(colorScheme.surfaceTint.toArgb()),
                argbToCssColor(colorScheme.inverseSurface.toArgb()),
                argbToCssColor(colorScheme.inverseOnSurface.toArgb()),
                argbToCssColor(colorScheme.error.toArgb()),
                argbToCssColor(colorScheme.onError.toArgb()),
                argbToCssColor(colorScheme.errorContainer.toArgb()),
                argbToCssColor(colorScheme.onErrorContainer.toArgb()),
                argbToCssColor(colorScheme.outline.toArgb()),
                argbToCssColor(colorScheme.outlineVariant.toArgb()),
                argbToCssColor(colorScheme.scrim.toArgb()),
            )
        }
    }
}

private fun argbToCssColor(argb: Int): String = String.format("#%06X", 0xFFFFFF and argb)