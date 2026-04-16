package org.grakovne.lissen.updater.api

import org.grakovne.lissen.updater.api.model.GitHubRelease
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubReleasesApi {
  @GET("repos/{owner}/{repo}/releases")
  suspend fun getReleases(
    @Path("owner") owner: String,
    @Path("repo") repo: String,
  ): List<GitHubRelease>
}
