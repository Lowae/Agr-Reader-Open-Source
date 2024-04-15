package com.lowae.agrreader.data.model.entities

data class Favicon(
    val url: String? = null,
    val icons: List<Icon>? = null
) {
    data class Icon(
        val url: String?,
        val width: Int?,
        val height: Int?,
        val format: String?,
        val bytes: Long?,
        val error: String?,
        val sha1sum: String?,
    )
}
