package com.samuelampadu.wikireader.data.remote

import com.samuelampadu.wikireader.domain.WikiLanguage
import java.net.URLEncoder

object WikiUrls {

    fun search(language: WikiLanguage): String =
        "https://${language.code}.wikipedia.org/w/api.php"

    /** Titles use underscores for spaces, and characters like "/" must be percent-encoded. */
    fun summary(language: WikiLanguage, title: String): String {
        val encoded = URLEncoder.encode(title.trim().replace(' ', '_'), "UTF-8").replace("+", "%20")
        return "https://${language.code}.wikipedia.org/api/rest_v1/page/summary/$encoded"
    }
}
