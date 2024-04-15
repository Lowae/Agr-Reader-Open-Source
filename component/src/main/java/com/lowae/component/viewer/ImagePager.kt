package com.lowae.component.viewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * pager组件
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageHorizonPager(
    // 编辑参数
    modifier: Modifier = Modifier,
    // pager状态
    state: PagerState,
    // 每个item之间的间隔
    itemSpacing: Dp = 0.dp,
    beyondBoundsPageCount: Int,
    // 页面内容
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        pageSpacing = itemSpacing,
        pageContent = pageContent ,
        beyondBoundsPageCount = beyondBoundsPageCount,
    )
}