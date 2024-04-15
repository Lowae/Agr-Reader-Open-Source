package com.lowae.agrreader.data.repository

import com.lowae.agrreader.data.model.preference.ProActivePreference
import com.lowae.agrreader.data.model.preference.TranslationOption
import com.lowae.agrreader.data.model.preference.TranslationOptionsPreference
import com.lowae.agrreader.data.model.preference.datastore.FeedSyncLimitCountPreference
import com.lowae.agrreader.data.model.preference.datastore.SingleDataStore
import com.lowae.agrreader.utils.ext.DataStore
import kotlinx.coroutines.flow.first

class FeedSyncPreferenceConfig private constructor(
    val activeCode: String,
    val limitCount: Int,
    val translationOption: TranslationOption
) {

    companion object {
        suspend fun create(): FeedSyncPreferenceConfig {
            val syncLimit =
                FeedSyncLimitCountPreference.fromPreferences(SingleDataStore.store.data.first()).value
            val defaultStorePreference = DataStore.data.first()
            return FeedSyncPreferenceConfig(
                ProActivePreference.fromPreferences(defaultStorePreference),
                syncLimit,
                TranslationOptionsPreference.fromPreferences(defaultStorePreference).value
            )
        }
    }

}