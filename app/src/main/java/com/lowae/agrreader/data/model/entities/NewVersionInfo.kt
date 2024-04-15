package com.lowae.agrreader.data.model.entities

import androidx.annotation.Keep
import com.lowae.agrreader.utils.compat.PackageManagerCompat

@Keep
data class NewVersionInfo(
    val newVersionCode: Long = PackageManagerCompat.versionCode,
    val lastCheckTime: Long = 0
)