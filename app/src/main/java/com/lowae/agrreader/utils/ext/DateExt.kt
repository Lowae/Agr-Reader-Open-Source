package com.lowae.agrreader.utils.ext

import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.formatAsString(
    context: Context,
    onlyHourMinute: Boolean? = false,
    atHourMinute: Boolean? = false,
): String {
    val locale = Locale.getDefault()
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    return when {
        onlyHourMinute == true -> {
            SimpleDateFormat("HH:mm", locale).format(this)
        }

        atHourMinute == true -> {
            context.getString(
                R.string.date_at_time,
                df.format(this),
                SimpleDateFormat("HH:mm", locale).format(this),
            )
        }

        else -> {
            df.format(this).run {
                when (this) {
                    df.format(Date()) -> context.getString(R.string.today)
                    df.format(
                        Calendar.getInstance().apply {
                            time = Date()
                            add(Calendar.DAY_OF_MONTH, -1)
                        }.time
                    ) -> context.getString(R.string.yesterday)

                    else -> this
                }
            }
        }
    }
}

fun Date.formatAsFlowString(
    context: Context = AgrReaderApp.application,
    showHour: Boolean = true
): String {
    val locale = Locale.getDefault()
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    val now = Calendar.getInstance()
    val apply = Calendar.getInstance().apply { time = this@formatAsFlowString }
    return if (now.get(Calendar.YEAR) == apply.get(Calendar.YEAR)) {
        if (now.get(Calendar.DAY_OF_YEAR) == apply.get(Calendar.DAY_OF_YEAR)) {
            if (showHour) SimpleDateFormat(
                "HH:mm",
                locale
            ).format(this) else context.getString(R.string.today)
        } else if (now.get(Calendar.DAY_OF_YEAR) == apply.get(Calendar.DAY_OF_YEAR) + 1) {
            context.getString(R.string.yesterday)
        } else {
            DateUtils.formatDateTime(
                context,
                this.time,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
            )
        }
    } else {
        df.format(this)
    }
}

fun Date.formatAtRecent(): String {
    val now = Calendar.getInstance()
    val apply = Calendar.getInstance().apply { time = this@formatAtRecent }
    return if (now.get(Calendar.DAY_OF_YEAR) == apply.get(Calendar.DAY_OF_YEAR)) {
        val nowHour = now.get(Calendar.HOUR_OF_DAY)
        val applyHour = apply.get(Calendar.HOUR_OF_DAY)
        if (nowHour == applyHour) {
            val nowMinute = now.get(Calendar.MINUTE)
            val applyMinute = now.get(Calendar.MINUTE)
            if (nowMinute - applyMinute < 1) {
                "刚刚"
            } else {
                "$${nowMinute - applyMinute} 分钟前"
            }
        } else {
            "${nowHour - applyHour} 小时前"
        }
    } else {
        SimpleDateFormat("MM-dd", Locale.getDefault()).format(apply.time)
    }
}

fun getStartOfDay(): Date {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant())
    } else {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}

fun getEndOfDay(): Date {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant())
    } else {
        Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}

fun getDayOfMonth() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)