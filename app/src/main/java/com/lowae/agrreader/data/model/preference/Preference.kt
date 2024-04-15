package com.lowae.agrreader.data.model.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import kotlinx.coroutines.CoroutineScope

abstract class Preference(protected val store: DataStore<Preferences> = DataStore) {
    abstract fun put(scope: CoroutineScope)
}

fun Preferences.toSettings(): Settings {
    return Settings(
        syncInterval = SyncIntervalPreference.fromPreferences(this),
        syncOnStart = SyncOnStartPreference.fromPreferences(this),
        syncOnlyOnWifi = SyncOnlyOnWiFiPreference.fromPreferences(this),
        syncOnlyWhenCharging = SyncOnlyWhenChargingPreference.fromPreferences(this),
        keepArchived = KeepArchivedPreference.fromPreferences(this),

        // Version
        newVersionNumber = NewVersionNumberPreference.fromPreferences(this),

        // Theme
        presetTheme = ThemeIndexPreference.fromPreferences(this),
        primaryColor = CustomPrimaryColorPreference.fromPreferences(this),
        darkTheme = DarkThemePreference.fromPreferences(this),
        dynamicColorTheme = DynamicColorPreference.fromPreferences(this),
        amoledDarkTheme = AmoledDarkThemePreference.fromPreferences(this),
        basicFonts = BasicFontsPreference.fromPreferences(this),
        feedGroupExpandState = FeedGroupExpandStatePreference.fromPreferences(this),
        feedLandscapeMode = FeedLandscapeModePreference.fromPreferences(this),
        articleItemStyle = ArticleItemStylePreference.fromPreferences(this),
        articleSortByOldest = FeedArticleSortByOldestPreference.fromPreferences(this),

        // Reading page
        readingToolbarAutoHide = ReadingToolbarAutoHidePreference.fromPreferences(this),
        readingLinkConfirm = ReadingLinkConfirmPreference.fromPreferences(this),
        readingTextFontSize = ReadingTextFontSizePreference.fromPreferences(this),
        readingTextFontWeight = ReadingTextFontWeightPreference.fromPreferences(this),
        readingLetterSpacing = ReadingLetterSpacingPreference.fromPreferences(this),
        readingLineHeight = ReadingLineHeightPreference.fromPreferences(this),
        readingTextHorizontalPadding = ReadingTextHorizontalPaddingPreference.fromPreferences(this),
        readingTextAlign = ReadingTextAlignPreference.fromPreferences(this),
        readingFonts = ReadingFontsPreference.fromPreferences(this),

        // Interaction
        initialFilter = InitialFilterPreference.fromPreferences(this),
        clickVibration = ClickVibrationPreference.fromPreferences(this),
        autoMarkReadOnScroll = AutoMarkReadOnScroll.fromPreferences(this),
        leftSwipeOperation = ArticleSwipePreference.LeftSwipe.fromPreferences(this),
        rightSwipeOperation = ArticleSwipePreference.RightSwipe.fromPreferences(this),
        translationOptions = TranslationOptionsPreference.fromPreferences(this),
        volumePageScroll = VolumePageScrollPreference.fromPreferences(this),
        markAllReadConfirm = MarkAllReadConfirmPreference.fromPreferences(this),
        feedOnlyCountUnread = FeedOnlyCountUnreadPreference.fromPreferences(this),
        // Languages
        languages = LanguagesPreference.fromPreferences(this),

        proActiveCode = ProActivePreference.fromPreferences(this)
    )
}
