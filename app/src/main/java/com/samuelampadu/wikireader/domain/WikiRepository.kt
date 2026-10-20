package com.samuelampadu.wikireader.domain

interface WikiRepository {
    suspend fun search(query: String, language: WikiLanguage): Result<List<SearchResult>>
    suspend fun summary(title: String, language: WikiLanguage): Result<ArticleSummary>
}
