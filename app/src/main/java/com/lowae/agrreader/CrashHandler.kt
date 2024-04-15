package com.lowae.agrreader

import android.content.Context
import android.os.Build
import android.util.Log
import com.lowae.agrreader.utils.compat.PackageManagerCompat
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler


/**
 * The uncaught exception handler for the application.
 */
class CrashHandler(private val context: Context) : UncaughtExceptionHandler {

    companion object {
        private const val TAG = "CrashHandler"
        private const val CRASH_LOG_FILE = "crash.log"

        fun getCrashFile(context: Context): File {
            return File(context.cacheDir.absolutePath, CRASH_LOG_FILE)
        }

    }

    private val originThreadUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * Catch all uncaught exception and log it.
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            handleException(e)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        originThreadUncaughtExceptionHandler?.uncaughtException(t, e)
    }

    private fun handleException(throwable: Throwable) {
        val logFile = getCrashFile(context)
        val fos = FileOutputStream(logFile)
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        val deviceInfo = """
                Device Info:
                Brand: ${Build.BRAND}
                Model: ${Build.MODEL}
                SDK Version: ${Build.VERSION.SDK_INT}
                Apk Version: ${PackageManagerCompat.versionCode}
                
                """.trimIndent()
        fos.write(deviceInfo.toByteArray())
        fos.write(sw.toString().toByteArray())
        fos.close()

        Log.e(TAG, "Unhandled exception caught, check log file: " + logFile.absolutePath)
    }
}
