package com.samuelampadu.wikireader.di

import com.samuelampadu.wikireader.data.DefaultWikiRepository
import com.samuelampadu.wikireader.data.remote.UserAgentInterceptor
import com.samuelampadu.wikireader.data.remote.WikipediaApi
import com.samuelampadu.wikireader.domain.WikiRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val USER_AGENT =
    "WikiReader/1.0 (https://github.com/sammyBorborbor/wiki-reader; owususamuel12@sansioglobal.com)"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(UserAgentInterceptor(USER_AGENT))
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideWikipediaApi(client: OkHttpClient, json: Json): WikipediaApi = Retrofit.Builder()
        // Calls pass a full URL per language; Retrofit still needs a base URL.
        .baseUrl("https://en.wikipedia.org/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(WikipediaApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWikiRepository(impl: DefaultWikiRepository): WikiRepository
}
