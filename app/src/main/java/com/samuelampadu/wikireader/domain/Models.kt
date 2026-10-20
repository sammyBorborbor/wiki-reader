package com.samuelampadu.wikireader.domain

data class SearchResult(
    val pageId: Long,
    val title: String,
    val snippet: String,
)

data class ArticleSummary(
    val title: String,
    val description: String?,
    val extract: String,
    val thumbnailUrl: String?,
    val pageUrl: String?,
)

/** Wikipedia editions the app can search. Each one has its own subdomain. */
enum class WikiLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    FRENCH("fr", "Français"),
    SWAHILI("sw", "Kiswahili"),
    TWI("tw", "Twi"),
    ARABIC("ar", "العربية");

    companion object {
        fun fromCode(code: String?): WikiLanguage = entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}
