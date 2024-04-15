package com.lowae.agrreader.ui.page.home.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.data.model.article.ArticleFlowItem

val DATE_STICKER_HEADER_HEIGHT = 26.dp

@Composable
fun StickyHeader(item: ArticleFlowItem.Date) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .height(DATE_STICKER_HEADER_HEIGHT)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 2.dp),
            text = item.dateString,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}