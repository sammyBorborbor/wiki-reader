package com.samuelampadu.wikireader.data

import com.samuelampadu.wikireader.data.remote.WikiUrls
import com.samuelampadu.wikireader.domain.WikiLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class WikiUrlsTest {

    @Test
    fun `search url uses the language subdomain`() {
        assertEquals("https://tw.wikipedia.org/w/api.php", WikiUrls.search(WikiLanguage.TWI))
    }

    @Test
    fun `summary url replaces spaces and encodes slashes`() {
        assertEquals(
            "https://en.wikipedia.org/api/rest_v1/page/summary/University_of_Ghana",
            WikiUrls.summary(WikiLanguage.ENGLISH, "University of Ghana"),
        )
        assertEquals(
            "https://en.wikipedia.org/api/rest_v1/page/summary/AC%2FDC",
            WikiUrls.summary(WikiLanguage.ENGLISH, "AC/DC"),
        )
    }
}
