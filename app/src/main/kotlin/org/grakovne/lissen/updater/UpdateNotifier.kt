package org.grakovne.lissen.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object UpdateNotifier {
  fun showUpdateNotification(
    context: Context,
    version: String,
    url: String,
    fileName: String,
  ) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
        NotificationChannel(
          "updater_channel",
          "App Updates",
          NotificationManager.IMPORTANCE_DEFAULT,
        )
      manager.createNotificationChannel(channel)
    }

    val intent =
      Intent(context, DownloadUpdateReceiver::class.java).apply {
        action = "org.grakovne.lissen.action.DOWNLOAD_UPDATE"
        putExtra("url", url)
        putExtra("fileName", fileName)
      }

    val pendingIntent =
      PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

    val notification =
      NotificationCompat
        .Builder(context, "updater_channel")
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Lissen Update Available")
        .setContentText("Version $version is available. Tap to download.")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    manager.notify(1001, notification)
  }
}
