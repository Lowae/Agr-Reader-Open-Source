package com.lowae.agrreader.ui.page.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lowae.agrreader.LocalNavHostController
import com.lowae.agrreader.MainViewModel
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.LocalDarkTheme
import com.lowae.agrreader.data.model.preference.LocalFeedLandscapeMode
import com.lowae.agrreader.ui.component.CheckProDialogContent
import com.lowae.agrreader.ui.page.history.HistoryPage
import com.lowae.agrreader.ui.page.home.HomeViewModel
import com.lowae.agrreader.ui.page.home.feeds.FeedsPage
import com.lowae.agrreader.ui.page.home.feeds.info.FeedInfoPage
import com.lowae.agrreader.ui.page.home.feeds.info.GroupInfoPage
import com.lowae.agrreader.ui.page.home.feeds.management.FeedManagementPage
import com.lowae.agrreader.ui.page.home.feeds.subscribe.SubscribePage
import com.lowae.agrreader.ui.page.home.flow.FlowPage
import com.lowae.agrreader.ui.page.home.reading.ReadingHorizonPage
import com.lowae.agrreader.ui.page.home.reading.ReadingPage
import com.lowae.agrreader.ui.page.home.reading.ReadingPageType
import com.lowae.agrreader.ui.page.home.reading.viewer.ReadingImageViewerPage
import com.lowae.agrreader.ui.page.landscape.FlowReadingPage
import com.lowae.agrreader.ui.page.landscape.TodayOfUnreadFlowReadingPage
import com.lowae.agrreader.ui.page.settings.SettingsPage
import com.lowae.agrreader.ui.page.settings.about.AboutPage
import com.lowae.agrreader.ui.page.settings.backup.BackupSettingPage
import com.lowae.agrreader.ui.page.settings.color.DisplayPage
import com.lowae.agrreader.ui.page.settings.interactive.InteractiveSettingPage
import com.lowae.agrreader.ui.page.settings.interactive.ReadingTranslatorSettingPage
import com.lowae.agrreader.ui.page.settings.navigation.NavigationAndFeedbackPage
import com.lowae.agrreader.ui.page.settings.server.RssServerSettingPage
import com.lowae.agrreader.ui.page.settings.universal.UniversalSettingPage
import com.lowae.agrreader.ui.page.today.TodayUnreadFlowPage
import com.lowae.agrreader.ui.theme.AppTheme
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.animatedComposable
import com.lowae.agrreader.utils.ext.animatedHorizontalSlideComposable
import com.lowae.agrreader.utils.ext.animatedScaleComposable
import com.lowae.agrreader.utils.ext.animatedVerticalSlideComposable
import com.lowae.agrreader.utils.ext.noAnimatedComposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeEntry(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val useDarkTheme = LocalDarkTheme.current.isDarkTheme()
    val navController = LocalNavHostController.current

    LaunchedEffect(Unit) {
        // This is finally
        navController.currentBackStackEntryFlow.collectLatest { entry ->
            if (entry.destination.route == RouteName.FEEDS) {
                homeViewModel.clear()
            }
            Log.i(
                "HomeEntry",
                "currentBackStackEntry: ${navController.currentDestination?.route}"
            )
        }
    }

    LaunchedEffect(mainViewModel) {
        launch {
            mainViewModel.openArticleIntentFlow.collect { articleId ->
                if (articleId.isEmpty()) return@collect
                RLog.i("HomeEntry", "openArticle: $articleId")
                ReadingRouter.navigate(navController, articleId) {
                    launchSingleTop = true
                }
            }
        }

        launch {
            mainViewModel.openFeedIntentFlow.collect { filterState ->
                RLog.i("HomeEntry", "openFeed: $filterState")
                FlowRouter.navigate(navController, listOf(filterState)) {
                    launchSingleTop = true
                }
            }
        }
    }

    AppTheme(
        useDarkTheme = useDarkTheme
    ) {
        rememberSystemUiController().run {
            setStatusBarColor(Color.Transparent, !useDarkTheme)
            setSystemBarsColor(Color.Transparent, !useDarkTheme)
            setNavigationBarColor(Color.Transparent, !useDarkTheme)
        }

        NavHost(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            navController = navController,
            startDestination = RouteName.FEEDS,
        ) {
            // Startup
//            animatedComposable(route = RouteName.STARTUP) {
//                StartupPage(navController)
//            }

            // Home
            noAnimatedComposable(route = RouteName.FEEDS) {
                FeedsPage(
                    navController = navController,
                    homeViewModel = homeViewModel
                )
            }
            animatedHorizontalSlideComposable(route = FlowRouter.routeUri) {
                if (LocalFeedLandscapeMode.current.value) {
                    FlowReadingPage()
                } else {
                    FlowPage(navController = navController)
                }
            }
            animatedHorizontalSlideComposable(route = RouteName.RECENTLY_READ) {
                HistoryPage(navController)
            }
            animatedHorizontalSlideComposable(route = TodayOfUnreadFlowRouter.routeUri) {
                if (LocalFeedLandscapeMode.current.value) {
                    TodayOfUnreadFlowReadingPage()
                } else {
                    TodayUnreadFlowPage(navController = navController)
                }
            }
            animatedVerticalSlideComposable(
                route = FeedInfoRouter.routeUri,
                arguments = FeedInfoRouter.arguments
            ) {
                FeedInfoPage(navController = navController)
            }
            animatedVerticalSlideComposable(
                route = GroupInfoRouter.routeUri,
                arguments = GroupInfoRouter.arguments
            ) {
                GroupInfoPage(navController)
            }

            animatedVerticalSlideComposable(route = RouteName.SUBSCRIBE) {
                SubscribePage(navController = navController)
            }
            animatedHorizontalSlideComposable(
                route = ReadingRouter.routeUri,
                arguments = ReadingRouter.arguments
            ) {
                ReadingPage(
                    navController,
                    articleId = it.arguments?.getString(ReadingRouter.arguments[0].name).orEmpty(),
                    type = ReadingPageType.SINGLE,
                    current = true
                )
            }

            animatedHorizontalSlideComposable(
                route = ReadingPagerRouter.routeUri,
                arguments = ReadingPagerRouter.arguments
            ) {
                ReadingHorizonPage(
                    navController,
                    articleId = it.arguments?.getString(ReadingRouter.arguments[0].name).orEmpty()
                )
            }

            // Settings
            animatedComposable(route = RouteName.SETTINGS) {
                SettingsPage(navController)
            }

            // Color & Style
            animatedComposable(route = RouteName.DISPLAY_SETTINGS) {
                DisplayPage(navController)
            }
            animatedComposable(route = RouteName.ABOUT_SETTINGS) {
                AboutPage(navController)
            }
            animatedScaleComposable(route = RouteName.READING_IMAGE_VIEWER) {
                ReadingImageViewerPage(navController)
            }
            animatedComposable(route = RouteName.UNIVERSAL_SETTINGS) {
                UniversalSettingPage(navController)
            }
            animatedComposable(route = RssServerSettingRouter.routeUri) {
                RssServerSettingPage(navController = navController)
            }
            animatedComposable(route = InteractiveSettingRouter.routeUri) {
                InteractiveSettingPage(navController = navController)
            }
            animatedComposable(route = InteractiveTranslatorSettingRouter.routeUri) {
                ReadingTranslatorSettingPage(navController = navController)
            }
            animatedComposable(route = NavigationAndFeedBackRouter.routeUri) {
                NavigationAndFeedbackPage(navController = navController)
            }

            animatedComposable(route = BackupSettingRouter.routeUri) {
                BackupSettingPage(navController = navController)
            }

            animatedComposable(route = RouteName.PRO_PAY) {
//                AgrProPage(navController = navController)
            }

            animatedHorizontalSlideComposable(route = FeedManagementRouter.routeUri) {
                FeedManagementPage(navController)
            }

            dialog(
                route = CheckProDialogRouter.routeUri,
                arguments = CheckProDialogRouter.arguments
            ) {
                val description = it.arguments?.getString(CheckProDialogRouter.arguments[0].name)
                    ?: stringResource(R.string.agr_reader_pro_dialog_content)
                CheckProDialogContent(navController, description)
            }
        }
    }
}
