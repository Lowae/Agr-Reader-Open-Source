package com.lowae.agrreader.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lowae.agrreader.utils.Base64Utils
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@Composable
fun FeedIcon(
    feedName: String,
    feedIcon: String?,
    size: Dp = 24.dp,
) {
    if (feedIcon.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                )
                .size(size),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = feedName.first().toString(),
                fontSize = (size.value / 2).sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    } else {
        val model = remember(feedIcon) {
            try {
                feedIcon.toHttpUrlOrNull() ?: Base64Utils.decode(feedIcon)
            } catch (e: Exception) {
                feedIcon
            }
        }
        val fallbackIcon = ColorPainter(MaterialTheme.colorScheme.primaryContainer)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentDescription = feedName,
            error = fallbackIcon,
            placeholder = fallbackIcon,
        )
    }
}

@Composable
@Preview
private fun FeedIcon1() {
    FeedIcon("测试", "")
}