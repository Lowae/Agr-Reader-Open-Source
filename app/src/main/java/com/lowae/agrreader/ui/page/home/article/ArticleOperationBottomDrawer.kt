package com.lowae.agrreader.ui.page.home.article

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.ui.component.base.LWBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleOperationBottomDrawer(
    articleWithFeed: ArticleWithFeed,
    onDismissRequest: () -> Unit,
    operations: List<ArticleOperationItem> = emptyList(),
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(true)
    val animateToDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }
    LWBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .align(Alignment.CenterHorizontally),
                text = articleWithFeed.article.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            operations.forEach { item ->
                OperationItem(
                    imageVector = item.icon,
                    title = item.title,
                    onClick = {
                        item.onClick?.invoke(articleWithFeed)
                        animateToDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun OperationItem(imageVector: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(imageVector = imageVector, contentDescription = title)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title)
    }
}