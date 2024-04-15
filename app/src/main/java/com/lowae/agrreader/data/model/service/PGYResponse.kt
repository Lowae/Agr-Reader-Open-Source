package com.lowae.agrreader.data.model.service

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class VersionInfo(
    @SerializedName("versionName")
    val versionName: String = "",
    @SerializedName("versionCode")
    val versionCode: Long = 0L,
    @SerializedName("changelog")
    val changelog: String = "",
    @SerializedName("fileKey")
    val fileKey: String = "",
    @SerializedName("url")
    val url: String = ""
)
