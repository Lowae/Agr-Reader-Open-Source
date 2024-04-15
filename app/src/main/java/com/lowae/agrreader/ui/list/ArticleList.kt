package com.lowae.agrreader.ui.list

import androidx.compose.runtime.Composable
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.component.base.paging.rememberPagingState

@Composable
fun ArticleList(
    items: List<ArticleFlowItem>,
    itemPresenter: ArticleItemPresenter,
    listPresenter: ArticleListPresenter
) {
//    val isLandscapeMode = LocalFeedLandscapeMode.current.value
//    if (isLandscapeMode) {
//        LandscapeArticleList(
//            listState = rememberStaggeredPagingState(
//                items.size,
//                onLoadMore = listPresenter.onLoadMore
//            ),
//            items = items,
//            itemPresenter = itemPresenter,
//            listPresenter = listPresenter,
//        )
//    } else {
//        NormalArticleList(
//            listState = rememberPagingState(
//                items.size,
//                onLoadMore = listPresenter.onLoadMore
//            ),
//            items = items,
//            itemPresenter = itemPresenter,
//            listPresenter = listPresenter,
//        )
//    }
    NormalArticleList(
        listState = rememberPagingState(
            items.size,
            onLoadMore = listPresenter.onLoadMore
        ),
        items = items,
        itemPresenter = itemPresenter,
        listPresenter = listPresenter,
    )
}