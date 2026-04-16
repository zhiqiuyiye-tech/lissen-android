package org.grakovne.lissen.updater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DownloadUpdateReceiver : BroadcastReceiver() {
  @Inject
  lateinit var apkDownloader: ApkDownloader

  override fun onReceive(
    context: Context,
    intent: Intent,
  ) {
    if (intent.action == "org.grakovne.lissen.action.DOWNLOAD_UPDATE") {
      val url = intent.getStringExtra("url") ?: return
      val fileName = intent.getStringExtra("fileName") ?: return
      apkDownloader.downloadAndInstall(url, fileName)
      Toast.makeText(context, "Update download started", Toast.LENGTH_SHORT).show()
    }
  }
}
