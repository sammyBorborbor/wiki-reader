package com.samuelampadu.wikireader

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class WikiReaderApp : Application(), ImageLoaderFactory {

    @Inject lateinit var okHttpClient: OkHttpClient

    /**
     * Coil would otherwise build its own OkHttpClient with the default "okhttp/x" User-Agent,
     * which Wikimedia's image servers reject with HTTP 403. Sharing the app's client sends ours.
     */
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .build()
}
