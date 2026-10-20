package com.samuelampadu.wikireader.fakes

import com.samuelampadu.wikireader.domain.ArticleSummary
import com.samuelampadu.wikireader.domain.SearchResult
import com.samuelampadu.wikireader.domain.WikiLanguage
import com.samuelampadu.wikireader.domain.WikiRepository
import kotlinx.coroutines.delay
import java.io.IOException

/** Virtual time under runTest, so it costs nothing in real time. */
private const val FAKE_NETWORK_DELAY_MS = 100L

class FakeWikiRepository(
    var results: List<SearchResult> = emptyList(),
    var shouldFail: Boolean = false,
) : WikiRepository {

    val searches = mutableListOf<Pair<String, WikiLanguage>>()

    override suspend fun search(query: String, language: WikiLanguage): Result<List<SearchResult>> {
        searches += query to language
        // Suspend like a real network call. Without this, Loading and the result are emitted
        // back to back and the conflated StateFlow only ever shows the result.
        delay(FAKE_NETWORK_DELAY_MS)
        return if (shouldFail) Result.failure(IOException("offline")) else Result.success(results)
    }

    override suspend fun summary(title: String, language: WikiLanguage): Result<ArticleSummary> =
        if (shouldFail) {
            Result.failure(IOException("offline"))
        } else {
            Result.success(ArticleSummary(title, null, "Extract", null, null))
        }
}
