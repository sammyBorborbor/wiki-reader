package com.samuelampadu.wikireader.data.remote

import com.samuelampadu.wikireader.data.remote.dto.SearchResponseDto
import com.samuelampadu.wikireader.data.remote.dto.SummaryDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Each Wikipedia language edition lives on its own subdomain, so calls take a full [Url]
 * built by [WikiUrls] instead of relying on a single base URL.
 */
interface WikipediaApi {

    @GET
    suspend fun search(
        @Url url: String,
        @Query("srsearch") query: String,
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("format") format: String = "json",
        @Query("formatversion") formatVersion: Int = 2,
        @Query("srlimit") limit: Int = 20,
    ): SearchResponseDto

    @GET
    suspend fun summary(@Url url: String): SummaryDto
}
