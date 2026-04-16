package org.grakovne.lissen.updater.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubReleaseAsset(
  @Json(name = "name") val name: String,
  @Json(name = "browser_download_url") val browserDownloadUrl: String,
)
