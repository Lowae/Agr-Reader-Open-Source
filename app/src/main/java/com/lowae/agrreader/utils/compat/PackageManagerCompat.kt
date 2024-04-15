package com.lowae.agrreader.utils.compat

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.lowae.agrreader.AgrReaderApp

object PackageManagerCompat {

    val versionCode: Long
        get() = getPackageInfo(AgrReaderApp.application.packageName).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                longVersionCode
            } else {
                @Suppress("DEPRECATION")
                versionCode.toLong()
            }
        }


    private fun getPackageInfo(packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AgrReaderApp.application.packageManager.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(
                    PackageManager.GET_META_DATA.toLong()
                )
            )
        } else {
            @Suppress("DEPRECATION")
            AgrReaderApp.application.packageManager.getPackageInfo(packageName, 0)
        }
    }

}