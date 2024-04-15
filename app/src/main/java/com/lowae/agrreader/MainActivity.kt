package com.lowae.agrreader

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.lowae.agrreader.data.model.preference.LanguagesPreference
import com.lowae.agrreader.data.model.preference.SettingsProvider
import com.lowae.agrreader.ui.page.common.ExtraName
import com.lowae.agrreader.ui.page.common.HomeEntry
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.Languages
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalNavHostController =
    compositionLocalOf<NavHostController> { error("No NavController found!") }

/**
 * The Single-Activity Architecture.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject
    lateinit var imageLoader: ImageLoader

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            RLog.i(TAG, "onCreate: ${intent?.extras?.getString(ExtraName.FEED_ID)} - ${intent?.extras?.getString(ExtraName.ARTICLE_ID)}")
            mainViewModel.openArticleIntent(intent?.getStringExtra(ExtraName.ARTICLE_ID).orEmpty())
            mainViewModel.openFeedIntent(intent?.getStringExtra(ExtraName.FEED_ID).orEmpty())
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Set the language
        LanguagesPreference.fromValue(Languages).let {
            if (it == LanguagesPreference.UseDeviceLanguages) return@let
            it.setLocale(this)
        }

        setContent {
            val navController = rememberNavController()
            CompositionLocalProvider(
                LocalImageLoader provides imageLoader,
                LocalNavHostController provides navController,
            ) {
                SettingsProvider {
                    HomeEntry(mainViewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        RLog.i(TAG, "onNewIntent: ${intent?.extras?.getString(ExtraName.FEED_ID)} - ${intent?.extras?.getString(ExtraName.ARTICLE_ID)}")
        mainViewModel.openArticleIntent(intent?.extras?.getString(ExtraName.ARTICLE_ID).orEmpty())
        mainViewModel.openFeedIntent(intent?.extras?.getString(ExtraName.FEED_ID).orEmpty())
    }
}
