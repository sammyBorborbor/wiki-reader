package com.samuelampadu.wikireader.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Wikimedia's API etiquette asks clients to send a descriptive User-Agent
 * with a way to contact the developer.
 */
class UserAgentInterceptor(private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request().newBuilder()
                .header("User-Agent", userAgent)
                .build()
        )
}
