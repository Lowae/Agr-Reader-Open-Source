package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class VolumePageScrollPreference(val value: Int) : Preference() {
    data object None : VolumePageScrollPreference(0)
    data object JUMP : VolumePageScrollPreference(1)
    data object ANIMATION : VolumePageScrollPreference(2)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            store.put(key, value)
        }
    }

    val toDesc
        get() = when (this) {
            ANIMATION -> "动画"
            JUMP -> "跳转"
            None -> "禁用"
        }
    val isEnable
        get() = this != None


    companion object : DataStoreKeys<Int> {

        val default = None
        val values = listOf(None, JUMP, ANIMATION)
        override val key: Preferences.Key<Int> = intPreferencesKey("VolumePageScrollPreference")

        fun fromPreferences(preferences: Preferences) =
            when (preferences[key]) {
                JUMP.value -> JUMP
                ANIMATION.value -> ANIMATION
                else -> default
            }
    }

}