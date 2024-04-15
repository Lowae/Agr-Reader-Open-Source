package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.caverock.androidsvg.SVG
import com.lowae.agrreader.data.model.preference.LocalDarkTheme
import com.lowae.agrreader.ui.svg.parseDynamicColor
import com.lowae.agrreader.ui.theme.palette.LocalTonalPalettes

@Composable
fun DynamicSVGImage(
    modifier: Modifier = Modifier,
    svgImageString: String,
) {
    val useDarkTheme = LocalDarkTheme.current.isDarkTheme()
    val tonalPalettes = LocalTonalPalettes.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    val svgPicture by remember(useDarkTheme, tonalPalettes, size) {
        mutableStateOf(
            SVG.getFromString(svgImageString.parseDynamicColor(tonalPalettes, useDarkTheme))
                .also {
                    it.documentWidth = size.width.toFloat()
                    it.documentHeight = size.height.toFloat()
                }
        )
    }
    Spacer(
        modifier
            .aspectRatio(1f)
            .onSizeChanged { size = it }
            .drawWithContent {
                svgPicture.renderToCanvas(this.drawContext.canvas.nativeCanvas)
            }
    )
}
