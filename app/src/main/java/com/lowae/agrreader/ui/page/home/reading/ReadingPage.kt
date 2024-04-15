package com.lowae.agrreader.ui.page.home.reading

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.entities.ArticleParserResult
import com.lowae.agrreader.data.model.entities.ImageSrcEntity
import com.lowae.agrreader.data.model.preference.LocalDarkTheme
import com.lowae.agrreader.data.model.preference.LocalReadingLinkConfirm
import com.lowae.agrreader.data.model.preference.LocalReadingToolbarAutoHide
import com.lowae.agrreader.data.model.preference.VolumePageScrollPreference
import com.lowae.agrreader.ui.page.common.ReadingImageViewerRouter
import com.lowae.agrreader.ui.page.home.reading.style.ReadingStyleBottomDrawer
import com.lowae.agrreader.ui.page.home.reading.webview.ReadingWebCallback
import com.lowae.agrreader.ui.theme.Shape20
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.openURL
import com.lowae.agrreader.utils.ext.surfaceColorAtElevation
import com.lowae.agrreader.utils.ext.toast
import com.lowae.component.constant.ElevationTokens
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPage(
    navController: NavHostController,
    articleId: String,
    readingViewModel: ReadingViewModel = hiltViewModel(key = articleId),
    type: ReadingPageType = ReadingPageType.SINGLE,
    current: Boolean = true,
    onPageScroll: (Boolean) -> Unit = {}
) {
    val context = navController.context
    val isLight = LocalDarkTheme.current.isDarkTheme().not()
    val autoHideToolbar = LocalReadingToolbarAutoHide.current.value
    val linkConfirm = LocalReadingLinkConfirm.current.value
    val colorScheme = MaterialTheme.colorScheme
    val readingUiState = readingViewModel.readingUiState.collectAsStateValue()
    val scope = rememberCoroutineScope()
    var toolBarVisible by remember(
        readingUiState.scrollableState,
        autoHideToolbar
    ) { mutableStateOf(true) }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(articleId) {
        RLog.d("ReadingPage", "articleId: $articleId")
        readingViewModel.webView.updateStyleConfig(colorScheme, isLight)
        if (readingUiState.articleWithFeed?.article?.id.isNullOrBlank() && articleId != readingUiState.articleWithFeed?.article?.id) {
            readingViewModel.initData(articleId, type)
        }
    }

    DisposableEffect(readingUiState.articleWithFeed, linkConfirm) {
        readingViewModel.webView.registerCallbacks(object : ReadingWebCallback {
            override fun onArticleParsed(result: ArticleParserResult) {
                readingViewModel.updateArticleParserResult(result)
            }

            override fun onArticleLoaded() {
                readingViewModel.hideLoading()
            }

            override fun onArticleImageClick(imageSrcEntity: ImageSrcEntity) {
                ReadingImageViewerRouter.navigate(navController, listOf(imageSrcEntity))
            }

            override fun onUrlClick(request: WebResourceRequest?) {
                val url = request?.url ?: return
                if (linkConfirm.not()) {
                    context.openURL(url.toString())
                } else {
                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        when (snackBarHostState.showSnackbar(
                            context.getString(R.string.reading_page_override_url_loading_toast),
                            context.getString(R.string.go),
                            true,
                            duration = SnackbarDuration.Short
                        )) {
                            SnackbarResult.Dismissed -> {}
                            SnackbarResult.ActionPerformed -> {
                                context.openURL(url.toString())
                            }
                        }
                    }
                }
            }
        })
        onDispose {
            readingViewModel.webView.unRegisterCallbacks()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState, snackbar = {
                Snackbar(
                    it,
                    shape = Shape20,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = ElevationTokens.Level2.dp
                    ),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.onSurface
                )
            })
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (readingUiState.articleWithFeed != null) {
                AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(1f),
                    visible = toolBarVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ReadingTopBar(
                        title = readingUiState.articleWithFeed.article.title,
                        link = readingUiState.articleWithFeed.article.link,
                        onClose = {
                            navController.popBackStack()
                        }
                    ) {
                        openBottomSheet = true
                    }
                }
                ReadingContent(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    readingUiState.scrollableState,
                    readingViewModel.webView,
                    isScrollingUp = {
                        if (autoHideToolbar) {
                            toolBarVisible = it
                        }
                    }
                )
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = toolBarVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .navigationBarsPadding()
                    ) {
                        if (readingUiState.isLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        BottomBar(
                            isStarred = readingUiState.articleWithFeed.article.isStarred,
                            pageState = readingUiState.pageState,
                            onStarred = {
                                readingViewModel.markStarred(it)
                            },
                            onFullContent = {
                                readingViewModel.showLoading()
                                readingViewModel.updateArticleContentSource(it)
                            },
                            onTranslate = {
                                if (readingUiState.articleWithFeed.feed.translationLanguage == null) {
                                    toast("翻译功能未开启，请前往订阅源设置中开启")
                                } else {
                                    readingViewModel.translate()
                                }
                            }
                        )
                    }
                }
            } else {
                CircularProgressIndicator(
                    Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
    VolumeButtonsPageScroller(
        key = readingUiState.scrollableState,
        enable = current,
        onPageUp = { volumePageScroll ->
            if (readingUiState.scrollableState.canScrollBackward) {
                scope.launch {
                    if (volumePageScroll == VolumePageScrollPreference.ANIMATION) {
                        readingUiState.scrollableState.animateScrollBy(readingViewModel.webView.height * 0.8f)
                    } else {
                        readingUiState.scrollableState.scrollBy(readingViewModel.webView.height * 0.8f)
                    }
                }
            } else {
                onPageScroll(true)
            }
        },
        onPageDown = { volumePageScroll ->
            if (readingUiState.scrollableState.canScrollForward) {
                scope.launch {
                    if (volumePageScroll == VolumePageScrollPreference.ANIMATION) {
                        readingUiState.scrollableState.animateScrollBy(-readingViewModel.webView.height * 0.8f)
                    } else {
                        readingUiState.scrollableState.scrollBy(-readingViewModel.webView.height * 0.8f)
                    }
                }
            } else {
                onPageScroll(false)
            }
        })
    ReadingStyleBottomDrawer(openBottomSheet) { openBottomSheet = false }
}