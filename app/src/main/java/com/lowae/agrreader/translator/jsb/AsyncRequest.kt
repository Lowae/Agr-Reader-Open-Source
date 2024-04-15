package com.lowae.agrreader.translator.jsb

fun interface AsyncRequest {
    fun request(workId: String, params: String, successCallback: Callback, failCallback: Callback)

    fun interface Callback {
        fun onResult(result: String)
    }
}