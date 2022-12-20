import model.EventSummary
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val getEventsDefaultJson = {}::class.java.getResource("/getEvents-default.json")!!.readText()
private val getEventsParametersJson = {}::class.java.getResource("/getEvents-parameters.json")!!.readText()

class TestGetEvents {
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
    fun testGetEventsWithDefaultParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse().setBody(getEventsDefaultJson))
        val client = Client("abc123", mockUrl)
        val result = client.getEvents()
        assertEquals(false, result.adult)
        assertEquals("America/Chicago", result.timezone)
        assertEquals(2, result.events.size)
        assertEquals(1, result.multidayStarting?.size)
        assertEquals(2, result.multidayOngoing?.size)
        assertEquals(EventSummary(
            id = "b80630ae75c35f34c0526173dd999cfc",
            name = "Cinco de Mayo",
            url = "https://www.checkiday.com/b80630ae75c35f34c0526173dd999cfc/cinco-de-mayo",
        ), result.events[0])
        val request = mockWebServer.takeRequest()
        assertEquals(1, request.requestUrl?.querySize)
        assertEquals("false", request.requestUrl?.queryParameter("adult"))
    }

    @Test
    fun testGetEventsWithSetParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse().setBody(getEventsParametersJson))
        val client = Client("abc123", mockUrl)
        val result = client.getEvents(
            date = "7/16/1992",
            adult = true,
            timezone = "America/New_York",
        )
        assertEquals(true, result.adult)
        assertEquals("America/New_York", result.timezone)
        assertEquals(2, result.events.size)
        assertEquals(0, result.multidayStarting?.size)
        assertEquals(1, result.multidayOngoing?.size)
        assertEquals(EventSummary(
            id = "6ebb6fd5e483de2fde33969a6c398472",
            name = "Get to Know Your Customers Day",
            url = "https://www.checkiday.com/6ebb6fd5e483de2fde33969a6c398472/get-to-know-your-customers-day",
        ), result.events[0])
        val request = mockWebServer.takeRequest()
        assertEquals(3, request.requestUrl?.querySize)
        assertEquals("true", request.requestUrl?.queryParameter("adult"))
        assertEquals("7/16/1992", request.requestUrl?.queryParameter("date"))
        assertEquals("America/New_York", request.requestUrl?.queryParameter("timezone"))
    }
}
