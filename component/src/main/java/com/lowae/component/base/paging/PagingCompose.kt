package com.lowae.component.base.paging

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

val EMPTY_LAZY_LIST_STATE = LazyListState(0, 0)
val EMPTY_LAZY_STAGGERED_LIST_STATE = LazyStaggeredGridState(0, 0)

@Composable
fun rememberPagingState(
    itemCount: Int,
    prefetch: Int = 10,
    onLoadMore: () -> Unit,
): LazyListState {
    val listState = if (itemCount > 0) rememberLazyListState() else EMPTY_LAZY_LIST_STATE
    LaunchedEffect(itemCount, listState) {
        snapshotFlow { listState.layoutInfo }
            .collect {
                if ((listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: 0) >= (listState.layoutInfo.totalItemsCount - prefetch)
                ) {
                    onLoadMore()
                }
            }
    }
    return listState
}

@Composable
fun rememberStaggeredPagingState(
    itemCount: Int,
    prefetch: Int = 10,
    onLoadMore: () -> Unit,
): LazyStaggeredGridState {
    val listState = if (itemCount > 0) rememberLazyStaggeredGridState() else EMPTY_LAZY_STAGGERED_LIST_STATE
    LaunchedEffect(itemCount, listState) {
        snapshotFlow { listState.layoutInfo }
            .collect {
                if ((listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: 0) >= (listState.layoutInfo.totalItemsCount - prefetch)
                ) {
                    onLoadMore()
                }
            }
    }
    return listState
}