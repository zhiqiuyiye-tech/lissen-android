package org.grakovne.lissen.updater

import org.grakovne.lissen.BuildConfig
import org.grakovne.lissen.persistence.preferences.LissenSharedPreferences
import org.grakovne.lissen.updater.api.GitHubReleasesApi
import org.grakovne.lissen.updater.api.model.GitHubRelease
import org.grakovne.lissen.updater.api.model.UpdateChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRepository
  @Inject
  constructor(
    private val api: GitHubReleasesApi,
    private val preferences: LissenSharedPreferences,
  ) {
    private val owner = "wulifan-ch"
    private val repo = "lissen-android"

    suspend fun checkUpdate(force: Boolean = false): Result<GitHubRelease?> {
      if (!force && !preferences.getAutoUpdateEnabled()) return Result.success(null)

      return try {
        val releases = api.getReleases(owner, repo)
        val channel = preferences.getUpdateChannel()

        val validReleases =
          releases.filter {
            if (channel == UpdateChannel.STABLE) !it.prerelease else true
          }

        val currentVersion = BuildConfig.VERSION_NAME.substringBefore('-')
        val latest = validReleases.firstOrNull()

        Result.success(
          latest?.let {
            val tagSemver = it.tagName.removePrefix("v")
            if (isHigher(tagSemver, currentVersion)) it else null
          },
        )
      } catch (e: Exception) {
        Result.failure(e)
      }
    }

    private fun isHigher(
      remote: String,
      local: String,
    ): Boolean {
      val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
      val localParts = local.split(".").mapNotNull { it.toIntOrNull() }

      val length = maxOf(remoteParts.size, localParts.size)
      for (i in 0 until length) {
        val r = remoteParts.getOrElse(i) { 0 }
        val l = localParts.getOrElse(i) { 0 }
        if (r > l) return true
        if (r < l) return false
      }
      return false
    }
  }
