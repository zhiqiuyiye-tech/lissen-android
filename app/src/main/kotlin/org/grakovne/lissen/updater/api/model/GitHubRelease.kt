package org.grakovne.lissen.updater.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubRelease(
  @Json(name = "name") val name: String,
  @Json(name = "tag_name") val tagName: String,
  @Json(name = "body") val body: String?,
  @Json(name = "prerelease") val prerelease: Boolean,
  @Json(name = "assets") val assets: List<GitHubReleaseAsset>,
)
