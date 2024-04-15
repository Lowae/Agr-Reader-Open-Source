package com.lowae.agrreader.ui.theme.palette.dynamic

import android.app.WallpaperManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.lowae.agrreader.data.model.preference.LocalPrimaryColor
import com.lowae.agrreader.ui.theme.palette.TonalPalettes
import com.lowae.agrreader.ui.theme.palette.TonalPalettes.Companion.getSystemTonalPalettes
import com.lowae.agrreader.ui.theme.palette.TonalPalettes.Companion.toTonalPalettes

enum class PresetColors(val color: Color) {
    RED(Color.Red),
    YELLOW(Color.Yellow),
    GREEN(Color.Green),
    BLUE(Color.Blue),
    VIOLET(Color(0xFF8000FF)),
    CUSTOM(Color.Transparent)
}

class BasicPalette(
    val preset: PresetColors,
    val tonalPalettes: TonalPalettes
)

@Composable
@Stable
fun extractTonalPalettesFromUserWallpaper(): List<TonalPalettes> {
    val context = LocalContext.current

    val preset: MutableList<TonalPalettes> = mutableListOf()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 && !LocalView.current.isInEditMode) {
        val colors = WallpaperManager.getInstance(LocalContext.current)
            .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
        val primary = colors?.primaryColor?.toArgb()
        val secondary = colors?.secondaryColor?.toArgb()
        val tertiary = colors?.tertiaryColor?.toArgb()
        if (primary != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                preset.add(context.getSystemTonalPalettes())
            } else {
                preset.add(Color(primary).toTonalPalettes())
            }
        }
        if (secondary != null) preset.add(Color(secondary).toTonalPalettes())
        if (tertiary != null) preset.add(Color(tertiary).toTonalPalettes())
    }
    return preset
}

@Stable
@Composable
fun extractBasicPalettesV2(): List<BasicPalette> =
    PresetColors.values().map {
        if (it == PresetColors.CUSTOM) {
            BasicPalette(it, LocalPrimaryColor.current.toTonalPalettes())
        } else {
            BasicPalette(it, it.color.toTonalPalettes())
        }
    }
