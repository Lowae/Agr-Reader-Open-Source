package com.lowae.agrreader.utils

import android.util.Base64

object Base64Utils {

    val regex = "/^data:\\s*([^\\s;,]+)\\s*;\\s*base64\\s*,/i".toRegex()

    fun decode(base64: String): ByteArray? {
        return Base64.decode(base64, Base64.DEFAULT)
    }

}