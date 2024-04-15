package com.lowae.agrreader.utils.ext

import org.json.JSONArray
import org.json.JSONObject

inline fun <R> JSONArray.map(transform: (JSONObject) -> R): List<R> =
    List(length()) { i ->
        transform(this.optJSONObject(i))
    }