package com.lowae.agrreader.utils

import com.lowae.agrreader.BuildConfig

object EnvUtils {

    val flavor: BuildFlavor
        get() = if (BuildConfig.FLAVOR == BuildFlavor.GOOGLE_PLAY.flavor) {
            BuildFlavor.GOOGLE_PLAY
        } else {
            BuildFlavor.OFFICIAL
        }

}

enum class BuildFlavor(val flavor: String) {
    OFFICIAL("Official"),
    GOOGLE_PLAY("GooglePlay")
}