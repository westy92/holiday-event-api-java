import model.EventSummary
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.lang.RuntimeException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val searchDefaultJson = {}::class.java.getResource("/search-default.json")!!.readText()
private val searchParametersJson = {}::class.java.getResource("/search-parameters.json")!!.readText()

class TestSearch {
    private lateinit var mockWebServer: MockWebServer

    @BeforeTest
    @Throws(IOException::class)
    fun setUp() {
        mockWebServer = MockWebServer().apply {
            start()
        }
    }

    @AfterTest
    @Throws(IOException::class)
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSearchWithDefaultParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse().setBody(searchDefaultJson))
        val client = Client("abc123", mockUrl)
        val result = client.search("zucchini")
        assertEquals(false, result.adult)
        assertEquals(3, result.events.size)
        assertEquals(
            EventSummary(
                id = "cc81cbd8730098456f85f69798cbc867",
                name = "National Zucchini Bread Day",
                url = "https://www.checkiday.com/cc81cbd8730098456f85f69798cbc867/national-zucchini-bread-day",
            ), result.events[0]
        )
        val request = mockWebServer.takeRequest()
        assertEquals(2, request.requestUrl?.querySize)
        assertEquals("zucchini", request.requestUrl?.queryParameter("query"))
        assertEquals("false", request.requestUrl?.queryParameter("adult"))
    }

    @Test
    fun testSearchWithSetParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse().setBody(searchParametersJson))
        val client = Client("abc123", mockUrl)
        val result = client.search(
            query = "porch day",
            adult = true,
        )
        assertEquals(true, result.adult)
        assertEquals(1, result.events.size)
        assertEquals(
            EventSummary(
                id = "61363236f06e4eb8e4e14e5925c2503d",
                name = "Sneak Some Zucchini Onto Your Neighbor's Porch Day",
                url = "https://www.checkiday.com/61363236f06e4eb8e4e14e5925c2503d/sneak-some-zucchini-onto-your-neighbors-porch-day",
            ), result.events[0]
        )
        val request = mockWebServer.takeRequest()
        assertEquals(2, request.requestUrl?.querySize)
        assertEquals("porch day", request.requestUrl?.queryParameter("query"))
        assertEquals("true", request.requestUrl?.queryParameter("adult"))
    }

    @Test
    fun testSearchQueryTooShort() {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(400)
            .setBody("{\"error\": \"Please enter a longer search term.\"}"))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.search("a")
        }
        assertEquals("Please enter a longer search term.", e.message)
        val request = mockWebServer.takeRequest()
        assertEquals(2, request.requestUrl?.querySize)
        assertEquals("a", request.requestUrl?.queryParameter("query"))
        assertEquals("false", request.requestUrl?.queryParameter("adult"))
    }

    @Test
    fun testSearchTooManyResults() {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(400)
            .setBody("{\"error\": \"Too many results returned. Please refine your query.\"}"))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.search("day")
        }
        assertEquals("Too many results returned. Please refine your query.", e.message)
        val request = mockWebServer.takeRequest()
        assertEquals(2, request.requestUrl?.querySize)
        assertEquals("day", request.requestUrl?.queryParameter("query"))
        assertEquals("false", request.requestUrl?.queryParameter("adult"))
    }

    @Test
    fun testSearchMissingParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.search("")
        }
        assertEquals("Search query is required.", e.message)
    }
}
