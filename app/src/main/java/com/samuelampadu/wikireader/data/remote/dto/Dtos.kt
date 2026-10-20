package com.samuelampadu.wikireader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Response of the MediaWiki Action API `list=search` query (formatversion=2). */
@Serializable
data class SearchResponseDto(
    val query: QueryDto? = null,
)

@Serializable
data class QueryDto(
    val search: List<SearchItemDto> = emptyList(),
)

@Serializable
data class SearchItemDto(
    @SerialName("pageid") val pageId: Long,
    val title: String,
    val snippet: String = "",
)

/** Response of the Wikimedia REST API `page/summary/{title}` endpoint. */
@Serializable
data class SummaryDto(
    val title: String,
    val description: String? = null,
    val extract: String = "",
    val thumbnail: ThumbnailDto? = null,
    @SerialName("content_urls") val contentUrls: ContentUrlsDto? = null,
)

@Serializable
data class ThumbnailDto(
    val source: String,
)

@Serializable
data class ContentUrlsDto(
    val mobile: PageUrlDto? = null,
)

@Serializable
data class PageUrlDto(
    val page: String,
)
