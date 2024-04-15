package com.lowae.agrreader.data.model.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.lowae.agrreader.data.model.entities.NewVersionInfo
import com.lowae.agrreader.ui.theme.palette.dynamic.PresetColors
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.collectAsStateValue
import kotlinx.coroutines.flow.map

data class Settings(
    val syncInterval: SyncIntervalPreference = SyncIntervalPreference.default,
    val syncOnStart: SyncOnStartPreference = SyncOnStartPreference.default,
    val syncOnlyOnWifi: SyncOnlyOnWiFiPreference = SyncOnlyOnWiFiPreference.default,
    val syncOnlyWhenCharging: SyncOnlyWhenChargingPreference = SyncOnlyWhenChargingPreference.default,
    val keepArchived: KeepArchivedPreference = KeepArchivedPreference.default,

    // Version
    val newVersionNumber: NewVersionInfo = NewVersionNumberPreference.default,

    // Theme
    val presetTheme: PresetColors = ThemeIndexPreference.default,
    val primaryColor: Color = CustomPrimaryColorPreference.DEFAULT,
    val darkTheme: DarkThemePreference = DarkThemePreference.default,
    val dynamicColorTheme: DynamicColorPreference = DynamicColorPreference.default,
    val amoledDarkTheme: AmoledDarkThemePreference = AmoledDarkThemePreference.default,
    val basicFonts: BasicFontsPreference = BasicFontsPreference.default,
    val feedGroupExpandState: FeedGroupExpandStatePreference = FeedGroupExpandStatePreference.default,
    val feedLandscapeMode: FeedLandscapeModePreference = FeedLandscapeModePreference.default,
    val articleItemStyle: ArticleItemStylePreference = ArticleItemStylePreference.default,
    val articleSortByOldest: FeedArticleSortByOldestPreference = FeedArticleSortByOldestPreference.default,

    // Reading page
    val readingToolbarAutoHide: ReadingToolbarAutoHidePreference = ReadingToolbarAutoHidePreference.default,
    val readingLinkConfirm: ReadingLinkConfirmPreference = ReadingLinkConfirmPreference.default,
    val readingTextFontSize: Int = ReadingTextFontSizePreference.default,
    val readingTextFontWeight: Int = ReadingTextFontWeightPreference.default,
    val readingLetterSpacing: Double = ReadingLetterSpacingPreference.default,
    val readingLineHeight: Double = ReadingLineHeightPreference.default,
    val readingTextHorizontalPadding: Int = ReadingTextHorizontalPaddingPreference.default,
    val readingTextAlign: ReadingTextAlignPreference = ReadingTextAlignPreference.default,
    val readingFonts: ReadingFontsPreference = ReadingFontsPreference.default,

    // Interaction
    val initialFilter: InitialFilterPreference = InitialFilterPreference.default,
    val clickVibration: ClickVibrationPreference = ClickVibrationPreference.default,
    val autoMarkReadOnScroll: AutoMarkReadOnScroll = AutoMarkReadOnScroll.default,
    val leftSwipeOperation: ArticleSwipePreference.LeftSwipe = ArticleSwipePreference.LeftSwipe.default,
    val rightSwipeOperation: ArticleSwipePreference.RightSwipe = ArticleSwipePreference.RightSwipe.default,
    val translationOptions: TranslationOptionsPreference = TranslationOptionsPreference.default,
    val volumePageScroll: VolumePageScrollPreference = VolumePageScrollPreference.default,
    val markAllReadConfirm: MarkAllReadConfirmPreference = MarkAllReadConfirmPreference.default,
    val feedOnlyCountUnread: FeedOnlyCountUnreadPreference = FeedOnlyCountUnreadPreference.default,

    // Languages
    val languages: LanguagesPreference = LanguagesPreference.default,

    val proActiveCode: String = ProActivePreference.DEFAULT
)

// Accounts
val LocalSyncInterval =
    compositionLocalOf<SyncIntervalPreference> { SyncIntervalPreference.default }
val LocalSyncOnStart = compositionLocalOf<SyncOnStartPreference> { SyncOnStartPreference.default }
val LocalSyncOnlyOnWiFi =
    compositionLocalOf<SyncOnlyOnWiFiPreference> { SyncOnlyOnWiFiPreference.default }
val LocalSyncOnlyWhenCharging =
    compositionLocalOf<SyncOnlyWhenChargingPreference> { SyncOnlyWhenChargingPreference.default }
val LocalKeepArchived =
    compositionLocalOf<KeepArchivedPreference> { KeepArchivedPreference.default }

// Version
val LocalNewVersionNumber = compositionLocalOf { NewVersionNumberPreference.default }

// Theme
val LocalPresetTheme =
    compositionLocalOf { ThemeIndexPreference.default }
val LocalPrimaryColor =
    compositionLocalOf { CustomPrimaryColorPreference.DEFAULT }
val LocalDarkTheme =
    compositionLocalOf<DarkThemePreference> { DarkThemePreference.default }
val LocalDynamicColorTheme =
    compositionLocalOf<DynamicColorPreference> { DynamicColorPreference.default }
val LocalAmoledDarkTheme =
    compositionLocalOf<AmoledDarkThemePreference> { AmoledDarkThemePreference.default }
val LocalBasicFonts = compositionLocalOf<BasicFontsPreference> { BasicFontsPreference.default }
val LocalFeedGroupExpandState =
    compositionLocalOf<FeedGroupExpandStatePreference> { FeedGroupExpandStatePreference.default }
val LocalFeedLandscapeMode: ProvidableCompositionLocal<FeedLandscapeModePreference> =
    compositionLocalOf { FeedLandscapeModePreference.default }

val LocalArticleItemStyle =
    compositionLocalOf<ArticleItemStylePreference> { ArticleItemStylePreference.default }
val LocalArticleSortByOldest =
    compositionLocalOf<FeedArticleSortByOldestPreference> { FeedArticleSortByOldestPreference.default }

// Reading page
val LocalReadingToolbarAutoHide: ProvidableCompositionLocal<ReadingToolbarAutoHidePreference> =
    compositionLocalOf { ReadingToolbarAutoHidePreference.default }
val LocalReadingLinkConfirm: ProvidableCompositionLocal<ReadingLinkConfirmPreference> =
    compositionLocalOf { ReadingLinkConfirmPreference.default }
val LocalReadingTextFontSize = compositionLocalOf { ReadingTextFontSizePreference.default }
val LocalReadingTextFontWeight = compositionLocalOf { ReadingTextFontWeightPreference.default }
val LocalReadingLetterSpacing = compositionLocalOf { ReadingLetterSpacingPreference.default }
val LocalReadingLineHeight = compositionLocalOf { ReadingLineHeightPreference.default }
val LocalReadingTextHorizontalPadding =
    compositionLocalOf { ReadingTextHorizontalPaddingPreference.default }
val LocalReadingTextAlign =
    compositionLocalOf<ReadingTextAlignPreference> { ReadingTextAlignPreference.default }
val LocalReadingFonts =
    compositionLocalOf<ReadingFontsPreference> { ReadingFontsPreference.default }

// Interaction
val LocalInitialFilter =
    compositionLocalOf<InitialFilterPreference> { InitialFilterPreference.default }
val LocalClickVibration =
    compositionLocalOf<ClickVibrationPreference> { ClickVibrationPreference.default }
val LocalAutoReadOnScroll =
    compositionLocalOf<AutoMarkReadOnScroll> { AutoMarkReadOnScroll.default }
val LocalArticleLeftSwipeOperation = compositionLocalOf { ArticleSwipePreference.LeftSwipe.default }
val LocalArticleRightSwipeOperation =
    compositionLocalOf { ArticleSwipePreference.RightSwipe.default }
val LocalTranslationOptions = compositionLocalOf { TranslationOptionsPreference.default }
val LocalVolumePageScroll: ProvidableCompositionLocal<VolumePageScrollPreference> =
    compositionLocalOf { VolumePageScrollPreference.default }
val LocalMarkAllReadConfirm: ProvidableCompositionLocal<MarkAllReadConfirmPreference> =
    compositionLocalOf { MarkAllReadConfirmPreference.default }
val LocalFeedOnlyCountUnread: ProvidableCompositionLocal<FeedOnlyCountUnreadPreference> =
    compositionLocalOf { FeedOnlyCountUnreadPreference.default }

// Languages
val LocalLanguages =
    compositionLocalOf<LanguagesPreference> { LanguagesPreference.default }

@Composable
fun SettingsProvider(
    content: @Composable () -> Unit,
) {
    val settings = remember {
        DataStore.data.map {
            RLog.d("SettingsProvider", "settings: $it")
            it.toSettings()
        }
    }.collectAsStateValue(initial = Settings())

    CompositionLocalProvider(
        LocalSyncInterval provides settings.syncInterval,
        LocalSyncOnStart provides settings.syncOnStart,
        LocalSyncOnlyOnWiFi provides settings.syncOnlyOnWifi,
        LocalSyncOnlyWhenCharging provides settings.syncOnlyWhenCharging,
        LocalKeepArchived provides settings.keepArchived,
        // Version
        LocalNewVersionNumber provides settings.newVersionNumber,
        LocalBasicFonts provides settings.basicFonts,

        // Theme
        LocalPresetTheme provides settings.presetTheme,
        LocalPrimaryColor provides settings.primaryColor,
        LocalDarkTheme provides settings.darkTheme,
        LocalDynamicColorTheme provides settings.dynamicColorTheme,
        LocalAmoledDarkTheme provides settings.amoledDarkTheme,
        LocalBasicFonts provides settings.basicFonts,
        LocalFeedGroupExpandState provides settings.feedGroupExpandState,
        LocalFeedLandscapeMode provides settings.feedLandscapeMode,
        LocalArticleItemStyle provides settings.articleItemStyle,
        LocalArticleSortByOldest provides settings.articleSortByOldest,

        // Reading page
        LocalReadingToolbarAutoHide provides settings.readingToolbarAutoHide,
        LocalReadingLinkConfirm provides settings.readingLinkConfirm,
        LocalReadingTextFontSize provides settings.readingTextFontSize,
        LocalReadingTextFontWeight provides settings.readingTextFontWeight,
        LocalReadingLetterSpacing provides settings.readingLetterSpacing,
        LocalReadingLineHeight provides settings.readingLineHeight,
        LocalReadingTextHorizontalPadding provides settings.readingTextHorizontalPadding,
        LocalReadingTextAlign provides settings.readingTextAlign,
        LocalReadingFonts provides settings.readingFonts,

        // Interaction
        LocalInitialFilter provides settings.initialFilter,
        LocalClickVibration provides settings.clickVibration,
        LocalAutoReadOnScroll provides settings.autoMarkReadOnScroll,
        LocalArticleLeftSwipeOperation provides settings.leftSwipeOperation,
        LocalArticleRightSwipeOperation provides settings.rightSwipeOperation,
        LocalTranslationOptions provides settings.translationOptions,
        LocalVolumePageScroll provides settings.volumePageScroll,
        LocalMarkAllReadConfirm provides settings.markAllReadConfirm,
        LocalFeedOnlyCountUnread provides settings.feedOnlyCountUnread,
        // Languages
        LocalLanguages provides settings.languages,
    ) {
        content()
    }
}

