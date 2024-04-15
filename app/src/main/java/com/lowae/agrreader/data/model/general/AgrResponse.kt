package com.lowae.agrreader.data.model.general

class AgrResponse<T>(
    val success: Boolean,
    val status: Int,
    val message: T
)