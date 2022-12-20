import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.lang.RuntimeException
import java.net.ConnectException
import kotlin.test.*

private val getEventsDefaultJson = {}::class.java.getResource("/getEvents-default.json")!!.readText()

class TestCommon {
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
    fun testSendsApiKey() {
        mockWebServer.enqueue(MockResponse()
            .setBody(getEventsDefaultJson))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        client.getEvents()
        val request = mockWebServer.takeRequest()
        assertEquals("abc123", request.headers["apikey"])
    }

    @Test
    fun testSendsUserAgent() {
        mockWebServer.enqueue(MockResponse()
            .setBody(getEventsDefaultJson))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        client.getEvents()
        val request = mockWebServer.takeRequest()
        assertEquals("HolidayApiJava/1.0.0", request.headers["user-agent"])
    }

    @Test
    fun testPassesAlongError() {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("{\"error\": \"MyError!\"}"))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.getEvents()
        }
        assertEquals("MyError!", e.message)
    }

    @Test
    fun testServerError500() {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("{}"))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.getEvents()
        }
        assertEquals("Server Error", e.message)
    }

    @Test
    fun testServerErrorUnknown() {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(599)
            .setBody("{}"))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.getEvents()
        }
        assertEquals("Server Error", e.message)
    }

    @Test
    fun testServerError() {
        mockWebServer.enqueue(MockResponse()
            .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        // TODO wrap exception?
        assertThrows<ConnectException> {
            client.getEvents()
        }
    }

    @Test
    fun testServerErrorMalformedResponse() {
        mockWebServer.enqueue(MockResponse()
            .setBody("{"))
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.getEvents()
        }
        assertEquals("Unable to parse response.", e.message)
    }

    @Test
    fun testFollowsRedirects() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(302)
            .setHeader("Location", "$mockUrl/redirected"))
        mockWebServer.enqueue(MockResponse()
            .setBody(getEventsDefaultJson))
        val client = Client("abc123", mockUrl)
        val result = client.getEvents()
        assertEquals("false", result.adult.toString())
    }

    @Test
    fun testReportsRateLimits() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse()
            .setHeader("X-RateLimit-Remaining-Month", "123")
            .setHeader("X-RateLimit-Limit-Month", "456")
            .setBody(getEventsDefaultJson))
        val client = Client("abc123", mockUrl)
        val result = client.getEvents()
        assertEquals(123, result.rateLimit.remainingMonth)
        assertEquals(456, result.rateLimit.limitMonth)
    }
}
