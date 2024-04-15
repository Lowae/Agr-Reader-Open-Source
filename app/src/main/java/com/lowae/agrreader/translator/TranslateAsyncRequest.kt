package com.lowae.agrreader.translator

import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.preference.TranslationOptionsPreference
import com.lowae.agrreader.translator.jsb.AsyncRequest
import com.lowae.agrreader.translator.jsb.AsyncRequestBody
import com.lowae.agrreader.translator.jsb.AsyncResponseBody
import com.lowae.agrreader.translator.jsb.HttpAsyncRequest
import com.lowae.agrreader.translator.request.TranslatorApi
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import kotlin.random.Random

class TranslateAsyncRequest(
    private val articleWithFeed: ArticleWithFeed,
    private val scope: CoroutineScope,
    okHttpClient: OkHttpClient
) : HttpAsyncRequest(scope, okHttpClient) {

    companion object {
        private const val WORK_ID_TRANSLATE = "Translation"
        private const val RANDOM_INTERVAL = 50L
        private const val REQUEST_INTERVAL = 100L
        private const val RETRY_INTERVAL = 1000L
        private const val RETRY_MAX_COUNT = 3
    }

    private val queue = Channel<TranslateJobInfo>(4)

    init {
        scope.launch {
            queue.receiveAsFlow()
                .onEach {
                    delay(
                        Random.nextLong(
                            REQUEST_INTERVAL - RANDOM_INTERVAL, REQUEST_INTERVAL + RANDOM_INTERVAL
                        )
                    )
                }
                .collect { jobParam ->
                    RLog.d(WORK_ID_TRANSLATE, "receive: ${jobParam.params}")
                    retryableFetch(jobParam)
                        .onSuccess {
                            withContext(Dispatchers.Main.immediate) {
                                jobParam.successCallback.onResult(
                                    GsonUtils.toJson(
                                        AsyncResponseBody(
                                            it
                                        )
                                    )
                                )
                            }
                        }.onFailure {
                            withContext(Dispatchers.Main.immediate) {
                                jobParam.failCallback.onResult(it.message.orEmpty())
                            }
                        }
                }
        }
    }

    override fun request(
        workId: String,
        params: String,
        successCallback: AsyncRequest.Callback,
        failCallback: AsyncRequest.Callback
    ) {
        if (workId != WORK_ID_TRANSLATE) {
            super.request(workId, params, successCallback, failCallback)
        } else {
            scope.launch {
                RLog.d(WORK_ID_TRANSLATE, "request: $params")
                val text = GsonUtils.fromJson<AsyncRequestBody>(params)?.request
                if (text.isNullOrEmpty()) {
                    failCallback.onResult("输入参数错误")
                } else {
                    queue.send(TranslateJobInfo(text, successCallback, failCallback))
                }
            }
        }
    }

    override suspend fun fetch(params: String): Result<String> {
        val (source, target) = articleWithFeed.feed.translationLanguage
            ?: return Result.failure(Exception("翻译功能未开启"))
        val translationOption =
            TranslationOptionsPreference.fromPreferences(DataStore.data.first()).value
        val translationResponse = TranslatorApi.getTranslator(translationOption)
            .request(TranslateRequest(params, source, target))
        return if (translationResponse == null) {
            Result.failure(Exception("翻译失败"))
        } else {
            Result.success(translationResponse.result)
        }
    }

    private suspend fun retryableFetch(info: TranslateJobInfo): Result<String> {
        val result = fetch(info.params)
        return if (result.isFailure && info.retryCount < RETRY_MAX_COUNT) {
            RLog.d(WORK_ID_TRANSLATE, "fetch fail: ${info.params}")
            delay(
                Random.nextLong(RETRY_INTERVAL - RANDOM_INTERVAL, RETRY_INTERVAL + RANDOM_INTERVAL)
            )
            info.retryCount++
            retryableFetch(info)
        } else {
            RLog.d(WORK_ID_TRANSLATE, "fetch success: ${info.params}")
            result
        }
    }

}

private class TranslateJobInfo(
    val params: String,
    val successCallback: AsyncRequest.Callback,
    val failCallback: AsyncRequest.Callback,
    var retryCount: Int = 0
)