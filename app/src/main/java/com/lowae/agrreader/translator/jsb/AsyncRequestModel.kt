package com.lowae.agrreader.translator.jsb

import androidx.annotation.Keep

@Keep
class AsyncRequestBody(
    val request: String,
)

@Keep
class AsyncResponseBody(
    val response: String
)