package com.lowae.agrreader.data.repository

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lowae.agrreader.MainActivity
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.ui.page.common.ExtraName
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Random
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    companion object {
        fun gotoNotificationSettings(context: Context) {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= 26) {
                // android 8.0引导
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            } else if (Build.VERSION.SDK_INT >= 21) {
                // android 5.0-7.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            } else {
                // 其他
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
                intent.setData(Uri.fromParts("package", context.packageName, null))
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    private val notificationName = context.getString(R.string.notification_name_article_update)

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    NotificationChannel(
                        context.packageName,
                        notificationName,
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        setShowBadge(true)
                    }
                )
            }
        }

    fun notify(feed: Feed, count: Int) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroup(
                NotificationChannelGroup(feed.id, feed.name)
            )
        }
        if (count == 0) return
        val builder = NotificationCompat.Builder(context, context.packageName)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(feed.name)
            .setContentText(
                context.getString(
                    R.string.notification_feed_subtitle,
                    count.toString()
                )
            )
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    Random().nextInt() + feed.id.hashCode(),
                    Intent(context, MainActivity::class.java).apply {
                        putExtra(ExtraName.FEED_ID, feed.id)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(
            Random().nextInt() + feed.id.hashCode(),
            builder.build()
        )
    }
}
