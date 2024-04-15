package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ArticleItemStylePreference(val value: Int) : Preference() {
    data object Default : ArticleItemStylePreference(0)
    data object Card : ArticleItemStylePreference(1)
    data object Text : ArticleItemStylePreference(2)
    data object Title : ArticleItemStylePreference(3)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.ArticleItemStyle, value)
        }
    }

    fun toDesc(context: Context = AgrReaderApp.application): String =
        when (this) {
            Default -> context.getString(R.string.defaults)
            Card -> context.getString(R.string.card)
            Text -> context.getString(R.string.text)
            Title -> context.getString(R.string.title)
        }


    companion object {

        val default = Default
        val values = listOf(
            Default,
            Card,
            Text,
            Title
        )

        fun fromPreferences(preferences: Preferences): ArticleItemStylePreference =
            when (preferences[DataStoreKeys.ArticleItemStyle.key]) {
                0 -> Default
                1 -> Card
                2 -> Text
                3 -> Title
                else -> Default
            }
    }
}