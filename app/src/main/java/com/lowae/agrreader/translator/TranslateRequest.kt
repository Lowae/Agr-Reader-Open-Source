package com.lowae.agrreader.translator

data class TranslateRequest(
    val text: String,
    val from: String,
    val to: String
)

data class TranslateResponse(
    val result: String
)