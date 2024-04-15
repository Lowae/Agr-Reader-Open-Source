package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lowae.agrreader.ui.theme.palette.onDark
import com.lowae.component.base.pullrefresh.PullRefreshIndicator
import com.lowae.component.base.pullrefresh.pullRefresh
import com.lowae.component.base.pullrefresh.rememberPullRefreshState

@Composable
fun PullRefresh(
    modifier: Modifier,
    refreshing: Boolean = false,
    enable: Boolean = true,
    onRefresh: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
) {
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh)

    Box(modifier.pullRefresh(pullRefreshState, enable)) {
        content()
        PullRefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surface onDark MaterialTheme.colorScheme.surfaceVariant,
            scale = true
        )
    }
}