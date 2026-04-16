package org.grakovne.lissen.updater.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.grakovne.lissen.updater.api.GitHubReleasesApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpdaterModule {
  @Provides
  @Singleton
  fun provideGitHubReleasesApi(): GitHubReleasesApi {
    val moshi = Moshi.Builder().build()

    val okHttpClient =
      okhttp3.OkHttpClient
        .Builder()
        .addInterceptor { chain ->
          val request =
            chain
              .request()
              .newBuilder()
              .header("User-Agent", "Lissen Android App")
              .header("Accept", "application/vnd.github.v3+json")
              .build()
          chain.proceed(request)
        }.build()

    val retrofit =
      Retrofit
        .Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    return retrofit.create(GitHubReleasesApi::class.java)
  }
}
