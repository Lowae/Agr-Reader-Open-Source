package com.lowae.agrreader.ui.page.landscape

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.ui.component.base.EmptyPlaceHolder
import com.lowae.agrreader.ui.page.home.flow.FlowPage
import com.lowae.agrreader.ui.page.home.flow.FlowViewModel
import com.lowae.agrreader.ui.page.home.reading.ReadingHorizonPage
import com.lowae.agrreader.ui.page.home.reading.ReadingHorizonPageViewModel
import com.lowae.agrreader.ui.page.today.TodayUnreadFlowPage
import com.lowae.agrreader.ui.page.today.TodayUnreadViewModel

@Composable
fun RowScope.FlowReadingLeftContent(
    navHostController: NavHostController,
    todayUnreadViewModel: TodayUnreadViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.3f)
    ) {
        TodayUnreadFlowPage(
            navController = navHostController,
            todayUnreadViewModel = todayUnreadViewModel
        )
    }
}

@Composable
fun RowScope.FlowReadingLeftContent(
    navHostController: NavHostController,
    flowViewModel: FlowViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.3f)
    ) {
        FlowPage(navController = navHostController, flowViewModel = flowViewModel)
    }
}

@Composable
fun RowScope.FlowReadingRightContent(
    navHostController: NavHostController,
    readingArticle: ArticleWithFeed?,
    readingHorizonPageViewModel: ReadingHorizonPageViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        if (readingArticle != null) {
            ReadingHorizonPage(
                navController = navHostController,
                articleId = readingArticle.article.id,
                readingHorizonPageViewModel = readingHorizonPageViewModel
            )
        } else {
            EmptyPlaceHolder(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}