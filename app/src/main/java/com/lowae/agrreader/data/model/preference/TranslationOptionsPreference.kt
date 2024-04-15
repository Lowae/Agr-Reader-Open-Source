package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TranslationOptionsPreference(val value: TranslationOption) : Preference() {

    companion object : DataStoreKeys<String> {
        val default = TranslationOptionsPreference(TranslationOption.GOOGLE_FREE)

        override val key: Preferences.Key<String> = stringPreferencesKey("TranslationOptionsKey")

        fun fromPreferences(preferences: Preferences): TranslationOptionsPreference {
            val value = preferences[key]
            val option = try {
                if (value.isNullOrBlank()) {
                    default.value
                } else {
                    TranslationOption.valueOf(value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                default.value
            }
            return TranslationOptionsPreference(option)
        }
    }

    override fun put(scope: CoroutineScope) {
        scope.launch {
            store.put(key, value.name)
        }
    }
}

enum class TranslationOption {
    GOOGLE_FREE,
    MICROSOFT_FREE,
    ;
//    DEEPL_FREE("DeepLFree");

    val title
        get() = when (this) {
            GOOGLE_FREE -> "Google"
            MICROSOFT_FREE -> "Microsoft"
        }

}