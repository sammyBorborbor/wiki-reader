package com.samuelampadu.wikireader.data

import com.samuelampadu.wikireader.data.remote.WikiUrls
import com.samuelampadu.wikireader.data.remote.WikipediaApi
import com.samuelampadu.wikireader.domain.ArticleSummary
import com.samuelampadu.wikireader.domain.SearchResult
import com.samuelampadu.wikireader.domain.WikiLanguage
import com.samuelampadu.wikireader.domain.WikiRepository
import com.samuelampadu.wikireader.util.stripHtml
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class DefaultWikiRepository @Inject constructor(
    private val api: WikipediaApi,
) : WikiRepository {

    override suspend fun search(query: String, language: WikiLanguage): Result<List<SearchResult>> =
        safeCall {
            api.search(url = WikiUrls.search(language), query = query)
                .query?.search.orEmpty()
                .map { SearchResult(pageId = it.pageId, title = it.title, snippet = it.snippet.stripHtml()) }
        }

    override suspend fun summary(title: String, language: WikiLanguage): Result<ArticleSummary> =
        safeCall {
            val dto = api.summary(WikiUrls.summary(language, title))
            ArticleSummary(
                title = dto.title,
                description = dto.description,
                extract = dto.extract,
                thumbnailUrl = dto.thumbnail?.source,
                pageUrl = dto.contentUrls?.mobile?.page,
            )
        }

    /** Like runCatching, but never swallows coroutine cancellation. */
    private inline fun <T> safeCall(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
