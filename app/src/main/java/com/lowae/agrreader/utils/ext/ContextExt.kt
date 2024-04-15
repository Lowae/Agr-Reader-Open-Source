package com.lowae.agrreader.utils.ext

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private var toast: Toast? = null

fun Context.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, message, duration)
    toast?.show()
}

fun Context.showToastLong(@StringRes stringRes: Int, vararg formatArgs: Any) {
    showToast(getString(stringRes, *formatArgs), Toast.LENGTH_LONG)
}

fun Context.showToastLong(message: String?) {
    showToast(message, Toast.LENGTH_LONG)
}

fun Context.showToastShort(@StringRes stringRes: Int, vararg formatArgs: Any) {
    showToast(getString(stringRes, *formatArgs), Toast.LENGTH_SHORT)
}

fun Context.showToastShort(message: String?) {
    showToast(message, Toast.LENGTH_SHORT)
}

fun toast(message: String?) {
    AgrReaderApp.application.showToast(message)
}

fun toast(@StringRes stringRes: Int, vararg formatArgs: Any?) {
    AgrReaderApp.application.showToast(AgrReaderApp.application.getString(stringRes, *formatArgs))
}

fun Context.share(title: String, content: String) {
    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }, getString(R.string.share)))
}

fun Context.openURL(url: String?) {
    url?.takeIf { it.trim().isNotEmpty() }
        ?.let {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it)
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (e: ActivityNotFoundException) {
                toast("未找到合适应用程序以打开链接")
                e.printStackTrace()
            }
        }
}

private val customTabsIntent = CustomTabsIntent.Builder().setShowTitle(true).build()

fun Context.openInCustomTabs(url: String) {
    try {
        customTabsIntent.launchUrl(this, url.toUri())
    } catch (e: Exception) {
        e.printStackTrace()
        openURL(url)
    }
}