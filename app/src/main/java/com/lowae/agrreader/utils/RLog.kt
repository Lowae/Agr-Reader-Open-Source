package com.lowae.agrreader.utils

import android.util.Log
import com.lowae.agrreader.BuildConfig

object RLog {
    private val isDebug = BuildConfig.DEBUG

    fun d(tag: String?, msg: String): Int {
        if (isDebug.not()) return -1
        return Log.d(tag, msg)
    }

    fun d(tag: String?, msg: String, tr: Throwable?): Int {
        if (isDebug.not()) return -1
        return Log.d(tag, msg, tr)
    }

    fun w(tag: String?, msg: String): Int {
        if (isDebug.not()) return -1
        return Log.w(tag, msg)
    }

    fun w(tag: String?, msg: String, tr: Throwable?): Int {
        if (isDebug.not()) return -1
        return Log.w(tag, msg, tr)
    }

    fun i(tag: String?, msg: String): Int {
        return Log.i(tag, msg)
    }

    fun i(tag: String?, msg: String, tr: Throwable?): Int {
        return Log.i(tag, msg, tr)
    }

    fun e(tag: String?, msg: String): Int {
        if (isDebug.not()) return -1
        return Log.e(tag, msg)
    }

    fun e(tag: String?, msg: String, tr: Throwable?): Int {
        if (isDebug.not()) return -1
        return Log.e(tag, msg, tr)
    }

}