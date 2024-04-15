package com.lowae.agrreader.utils.ext

import com.lowae.agrreader.AgrReaderApp

fun getString(resId: Int, vararg args: Any) = AgrReaderApp.application.getString(resId, *args)