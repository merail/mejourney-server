package app.mejourney

import kotlin.test.Test
import kotlin.test.assertEquals

class ImageUrlsTest {

    @Test
    fun `sorts numerically by the trailing number in the filename`() {
        val urls = listOf(
            "/images/contents/content_11/content_10.HEIC",
            "/images/contents/content_11/content_2.HEIC",
            "/images/contents/content_11/content_1.HEIC",
        )

        assertEquals(
            listOf(
                "/images/contents/content_11/content_1.HEIC",
                "/images/contents/content_11/content_2.HEIC",
                "/images/contents/content_11/content_10.HEIC",
            ),
            sortImageUrls(urls),
        )
    }

    @Test
    fun `url without a number sorts before numbered ones`() {
        assertEquals(
            listOf(
                "/images/covers/home_cover.HEIC",
                "/images/contents/content_11/content_1.HEIC",
            ),
            sortImageUrls(
                listOf(
                    "/images/contents/content_11/content_1.HEIC",
                    "/images/covers/home_cover.HEIC",
                ),
            ),
        )
    }
}
