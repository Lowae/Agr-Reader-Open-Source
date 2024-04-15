package com.lowae.agrreader.ui.component.base

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RYAsyncImage(
    modifier: Modifier = Modifier,
    data: Any? = null,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String = "",
    placeholder: Painter? = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
    error: Painter? = placeholder,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current).data(data)
            .crossfade(true)
            .allowRgb565(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = placeholder,
        error = error,
        contentScale = contentScale,
    )
}