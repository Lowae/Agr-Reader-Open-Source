package com.lowae.agrreader.data.model.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ImageSrcEntity(
    val images: List<String> = emptyList(),
    val index: Int = 0,
) : Parcelable