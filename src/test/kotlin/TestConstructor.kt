import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestConstructor {
    @Test
    fun testBlankApiKey() {
        val e = assertThrows<IllegalArgumentException> {
            Client("")
        }
        assertEquals("Please provide a valid API key. Get one at https://apilayer.com/marketplace/checkiday-api#pricing.", e.message)
    }

    @Test
    fun testInvalidBaseUrl() {
        val e = assertThrows<IllegalArgumentException> {
            Client("abc123", "")
        }
        assertEquals("Invalid baseUrl.", e.message)
    }

    @Test
    fun testConstructorSuccess() {
        val client = Client("apikey")
        assertNotNull(client)
    }
}
