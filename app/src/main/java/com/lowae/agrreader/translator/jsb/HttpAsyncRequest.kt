package com.lowae.agrreader.translator.jsb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

abstract class HttpAsyncRequest(
    private val scope: CoroutineScope,
    private val okHttpClient: OkHttpClient
) : AsyncRequest {

    override fun request(
        workId: String,
        params: String,
        successCallback: AsyncRequest.Callback,
        failCallback: AsyncRequest.Callback
    ) {
        scope.launch {
            fetch(params).onSuccess {
                withContext(Dispatchers.Main.immediate) {
                    successCallback.onResult(it)
                }
            }.onFailure {
                withContext(Dispatchers.Main.immediate) {
                    failCallback.onResult(it.message.orEmpty())
                }
            }
        }
    }

    abstract suspend fun fetch(params: String): Result<String>

}