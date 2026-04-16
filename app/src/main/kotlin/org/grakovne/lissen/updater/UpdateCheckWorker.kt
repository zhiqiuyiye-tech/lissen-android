package org.grakovne.lissen.updater

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class UpdateCheckWorker(
  appContext: Context,
  workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface UpdaterEntryPoint {
    fun updateRepository(): UpdateRepository

    fun apkDownloader(): ApkDownloader
  }

  override suspend fun doWork(): Result {
    val force = inputData.getBoolean("force", false)
    val entryPoint = EntryPointAccessors.fromApplication(applicationContext, UpdaterEntryPoint::class.java)
    val updateRepo = entryPoint.updateRepository()
    val apkDownloader = entryPoint.apkDownloader()

    val release = updateRepo.checkUpdate(force = force).getOrNull()
    if (release != null) {
      val apkAsset = release.assets.firstOrNull { it.name.endsWith(".apk") }
      if (apkAsset != null) {
        UpdateNotifier.showUpdateNotification(applicationContext, release.tagName, apkAsset.browserDownloadUrl, apkAsset.name)
      }
    }

    return Result.success()
  }
}
