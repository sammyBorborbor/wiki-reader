package com.samuelampadu.wikireader.util

import org.junit.Assert.assertEquals
import org.junit.Test

class HtmlTextTest {

    @Test
    fun `removes search highlight markup`() {
        val snippet = "<span class=\"searchmatch\">Accra</span> is the capital of Ghana"
        assertEquals("Accra is the capital of Ghana", snippet.stripHtml())
    }

    @Test
    fun `decodes common entities without double decoding`() {
        assertEquals("Tom & Jerry \"classic\" 'show'", "Tom &amp; Jerry &quot;classic&quot; &#039;show&#039;".stripHtml())
        assertEquals("&lt;", "&amp;lt;".stripHtml())
    }
}
