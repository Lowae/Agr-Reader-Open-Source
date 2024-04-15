package com.lowae.agrreader.data.model.preference

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.getString
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


enum class ArticleSwipeOperation(val id: Int) {
    NONE(0),
    READ(1),
    STAR(2);


    fun toDesc(): String {
        return when (this) {
            NONE -> getString(R.string.article_swipe_operation_none)
            READ -> getString(R.string.article_swipe_operation_read)
            STAR -> getString(R.string.article_swipe_operation_star)
        }
    }

    fun icon(selected: Boolean): ImageVector? {
        return when (this) {
            NONE -> null
            READ -> if (selected) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle
            STAR -> if (selected) Icons.Rounded.Star else Icons.Outlined.Star
        }
    }

}

sealed interface ArticleSwipePreference {

    class LeftSwipe(val value: ArticleSwipeOperation) : Preference(), ArticleSwipePreference {

        override fun put(scope: CoroutineScope) {
            scope.launch {
                DataStore.put(DataStoreKeys.ArticleLeftSwipeKey, value.id)
            }
        }

        companion object {
            val default = LeftSwipe(ArticleSwipeOperation.READ)

            fun fromPreferences(preferences: Preferences): LeftSwipe {
                val value = preferences[DataStoreKeys.ArticleLeftSwipeKey]
                return ArticleSwipeOperation.entries.find { it.id == value }?.let { LeftSwipe(it) }
                    ?: default
            }
        }
    }

    class RightSwipe(val value: ArticleSwipeOperation) : Preference(), ArticleSwipePreference {
        override fun put(scope: CoroutineScope) {
            scope.launch {
                DataStore.put(DataStoreKeys.ArticleRightSwipeKey, value.id)
            }
        }

        companion object {
            val default = RightSwipe(ArticleSwipeOperation.STAR)

            fun fromPreferences(preferences: Preferences): RightSwipe {
                val value = preferences[DataStoreKeys.ArticleRightSwipeKey]
                return ArticleSwipeOperation.entries.find { it.id == value }?.let { RightSwipe(it) }
                    ?: default
            }

        }
    }
}
