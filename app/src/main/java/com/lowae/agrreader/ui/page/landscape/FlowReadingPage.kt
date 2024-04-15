package com.lowae.agrreader.ui.page.landscape

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.lowae.agrreader.LocalNavHostController
import com.lowae.agrreader.ui.page.home.flow.FlowViewModel
import com.lowae.agrreader.ui.page.home.reading.ReadingHorizonPageViewModel
import com.lowae.agrreader.ui.page.today.TodayUnreadViewModel
import com.lowae.agrreader.utils.ext.collectAsStateValue

@Composable
fun FlowReadingPage(
    flowViewModel: FlowViewModel = hiltViewModel(),
    readingHorizonPageViewModel: ReadingHorizonPageViewModel = hiltViewModel(),
) {
    val navHostController = LocalNavHostController.current
    val flowUiState = flowViewModel.flowUiState.collectAsStateValue()
    val readingArticle = flowUiState.readingArticleState.collectAsStateValue()
    Row {
        FlowReadingLeftContent(
            navHostController = navHostController,
            flowViewModel = flowViewModel
        )
        FlowReadingRightContent(
            navHostController = navHostController,
            readingArticle = readingArticle,
            readingHorizonPageViewModel = readingHorizonPageViewModel
        )
    }
}

@Composable
fun TodayOfUnreadFlowReadingPage(
    todayUnreadViewModel: TodayUnreadViewModel = hiltViewModel(),
    readingHorizonPageViewModel: ReadingHorizonPageViewModel = hiltViewModel(),
) {
    val navHostController = LocalNavHostController.current
    val todayUnreadUiState = todayUnreadViewModel.todayUnreadUIState.collectAsStateValue()
    val readingArticle = todayUnreadUiState.readingArticleState.collectAsStateValue()
    Row {
        FlowReadingLeftContent(
            navHostController = navHostController,
            todayUnreadViewModel = todayUnreadViewModel
        )
        FlowReadingRightContent(
            navHostController = navHostController,
            readingArticle = readingArticle,
            readingHorizonPageViewModel = readingHorizonPageViewModel
        )
    }
}