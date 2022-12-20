import model.Occurrence
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.lang.RuntimeException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val getEventInfoDefaultJson = {}::class.java.getResource("/getEventInfo.json")!!.readText()
private val getEventInfoParametersJson = {}::class.java.getResource("/getEventInfo-parameters.json")!!.readText()

class TestGetEventInfo {
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
    fun testGetEventInfoWithDefaultParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse().setBody(getEventInfoDefaultJson))
        val client = Client("abc123", mockUrl)
        val result = client.getEventInfo("f90b893ea04939d7456f30c54f68d7b4")
        assertEquals(2, result.event.hashtags?.size)
    }

    @Test
    fun testGetEventInfoWithSetParameters() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse().setBody(getEventInfoParametersJson))
        val client = Client("abc123", mockUrl)
        val result = client.getEventInfo(
            id = "f90b893ea04939d7456f30c54f68d7b4",
            start = 2002,
            end = 2003,
        )
        assertEquals(2, result.event.occurrences?.size)
        assertEquals(
            Occurrence(
                date = "08/08/2002",
                length = 1,
            ), result.event.occurrences?.get(0)
        )
    }

    @Test
    fun testGetEventInfoInvalidEvent() {
        val mockUrl = mockWebServer.url("/").toString()
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(404)
            .setBody("{\"error\":\"Event not found.\"}"))
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.getEventInfo("hi")
        }
        assertEquals("Event not found.", e.message)
    }

    @Test
    fun testGetEventInfoMissingId() {
        val mockUrl = mockWebServer.url("/").toString()
        val client = Client("abc123", mockUrl)
        val e = assertThrows<RuntimeException> {
            client.getEventInfo("")
        }
        assertEquals("Event id is required.", e.message)
    }
}
