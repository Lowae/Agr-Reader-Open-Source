package com.lowae.agrreader.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

object GsonUtils {

    val gson by lazy { Gson() }

    inline fun <reified T> fromJson(json: String?): T? {
        return try {
            gson.fromJson<T>(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    inline fun <reified T> fromJson(json: Reader): T? {
        return try {
            gson.fromJson<T>(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun toJson(any: Any): String = gson.toJson(any)

}