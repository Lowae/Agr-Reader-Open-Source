package com.lowae.agrreader.data.provider

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lowae.agrreader.data.module.UserAgentInterceptor
import okhttp3.OkHttpClient

abstract class BaseAPI2 {

    protected val client: OkHttpClient = OkHttpClient()
        .newBuilder()
        .addNetworkInterceptor(UserAgentInterceptor)
        .build()

    protected val gson: Gson = GsonBuilder().create()

    protected inline fun <reified T> toDTO(jsonStr: String): T =
        gson.fromJson(jsonStr, T::class.java)!!
}

class ArticleStatusIds(
    val unreadIds: MutableCollection<String>,
    val starredIds: MutableCollection<String>
)