package com.lowae.agrreader.data.model.entities

import com.google.gson.annotations.SerializedName

data class ArticleParserResult(
    val result: Int = -1,
    @SerializedName("cover")
    val cover: String? = null,
    @SerializedName("article")
    val article: String? = null,
) {
    companion object {
        const val RESULT_CODE_ERROR = -1
        const val RESULT_CODE_SUCCESS = 0
    }
}