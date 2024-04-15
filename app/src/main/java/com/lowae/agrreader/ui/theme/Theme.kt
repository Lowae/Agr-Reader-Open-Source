package com.lowae.agrreader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.lowae.agrreader.data.model.preference.LocalBasicFonts
import com.lowae.agrreader.data.model.preference.LocalDynamicColorTheme
import com.lowae.agrreader.data.model.preference.LocalPresetTheme
import com.lowae.agrreader.data.model.preference.LocalPrimaryColor
import com.lowae.agrreader.ui.theme.palette.LocalTonalPalettes
import com.lowae.agrreader.ui.theme.palette.TonalPalettes
import com.lowae.agrreader.ui.theme.palette.TonalPalettes.Companion.toTonalPalettes
import com.lowae.agrreader.ui.theme.palette.core.ProvideZcamViewingConditions
import com.lowae.agrreader.ui.theme.palette.dynamic.PresetColors
import com.lowae.agrreader.ui.theme.palette.dynamic.extractTonalPalettesFromUserWallpaper
import com.lowae.agrreader.ui.theme.palette.toDarkColorScheme
import com.lowae.agrreader.ui.theme.palette.toLightColorScheme

@Composable
fun AppTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val dynamicColorTheme = LocalDynamicColorTheme.current.value
    val wallpaperPalettes: List<TonalPalettes> = extractTonalPalettesFromUserWallpaper()
    val tonalPalettes = if (dynamicColorTheme.not()) {
        val themePresetColor = LocalPresetTheme.current
        if (themePresetColor == PresetColors.CUSTOM) {
            LocalPrimaryColor.current.toTonalPalettes()
        } else {
            themePresetColor.color.toTonalPalettes()
        }
    } else {
        wallpaperPalettes.firstOrNull() ?: LocalPrimaryColor.current.toTonalPalettes()
    }

    ProvideZcamViewingConditions {
        CompositionLocalProvider(
            LocalTonalPalettes provides tonalPalettes.apply { Preparing() },
        ) {
            MaterialTheme(
                colorScheme = LocalTonalPalettes.current.run { if (useDarkTheme) toDarkColorScheme() else toLightColorScheme() },
                typography = LocalBasicFonts.current.asTypography(LocalContext.current),
                shapes = Shapes,
                content = content,
            )
        }
    }
}
