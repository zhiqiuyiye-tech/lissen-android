package org.grakovne.lissen.updater

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkDownloader
  @Inject
  constructor(
    @ApplicationContext private val context: Context,
  ) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadId: Long = -1L
    private var lastFileTarget: File? = null

    private val receiver =
      object : BroadcastReceiver() {
        override fun onReceive(
          context: Context?,
          intent: Intent?,
        ) {
          val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
          if (id == downloadId && id != -1L) {
            installApk()
            runCatching { context?.unregisterReceiver(this) }
          }
        }
      }

    fun downloadAndInstall(
      url: String,
      fileName: String,
    ) {
      val request =
        DownloadManager
          .Request(Uri.parse(url))
          .setTitle("Lissen Update")
          .setDescription("Downloading newer version $fileName")
          .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
          .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)

      lastFileTarget = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
      if (lastFileTarget?.exists() == true) {
        lastFileTarget?.delete()
      }

      ContextCompat.registerReceiver(
        context,
        receiver,
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
        ContextCompat.RECEIVER_EXPORTED,
      )
      downloadId = downloadManager.enqueue(request)
    }

    private fun installApk() {
      val file = lastFileTarget ?: return
      if (!file.exists()) return

      val uri =
        FileProvider.getUriForFile(
          context,
          "${context.packageName}.update_provider",
          file,
        )

      val intent =
        Intent(Intent.ACTION_VIEW).apply {
          setDataAndType(uri, "application/vnd.android.package-archive")
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

      context.startActivity(intent)
    }
  }
