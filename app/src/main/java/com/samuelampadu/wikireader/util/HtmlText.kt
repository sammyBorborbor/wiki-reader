package com.samuelampadu.wikireader.util

private val tagPattern = Regex("<[^>]*>")

/** Search snippets contain highlight markup like <span class="searchmatch">. */
fun String.stripHtml(): String =
    replace(tagPattern, "")
        .replace("&quot;", "\"")
        .replace("&#039;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .trim()
