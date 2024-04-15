package com.lowae.agrreader.data.provider

class RssServiceException(override val message: String, val code: Int = 0) : Exception(message) {

    companion object {
        const val ERR_UNAUTHORIZED = 401
        const val ERR_FORBIDDEN = 403
    }

}