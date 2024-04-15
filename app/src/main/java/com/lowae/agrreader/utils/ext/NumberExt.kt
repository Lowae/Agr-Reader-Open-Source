package com.lowae.agrreader.utils.ext


fun Int.spacerDollar(str: Any): String = "$this$$str"

fun String.dollarLast(): String = split("$").last()

fun Int.toBoolean(): Boolean = this != 0