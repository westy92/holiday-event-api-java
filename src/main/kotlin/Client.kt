import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import model.GetEventInfoResponse
import model.GetEventsResponse
import model.SearchResponse
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class Client(
    private val apiKey: String,
    baseUrl: String = "https://api.apilayer.com/checkiday"
) {
    private val client: OkHttpClient = OkHttpClient()
    private val baseUrl: HttpUrl
    private val mapper = ObjectMapper().registerKotlinModule().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    init {
        if (apiKey.isEmpty()) {
            throw IllegalArgumentException("Please provide a valid API key. Get one at https://apilayer.com/marketplace/checkiday-api#pricing.")
        }
        this.baseUrl = baseUrl.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid baseUrl.")
    }

    fun getEvents(date: String? = null, adult: Boolean = false, timezone: String? = null): GetEventsResponse {
        val params = mutableMapOf(
            "adult" to adult.toString(),
        )
        if (date != null) {
            params["date"] = date
        }
        if (timezone != null) {
            params["timezone"] = timezone
        }
        return request("events", params, GetEventsResponse::class.java)
    }

    fun getEventInfo(id: String, start: Int? = null, end: Int? = null): GetEventInfoResponse {
        if (id.isEmpty()) {
            throw IllegalArgumentException("Event id is required.")
        }
        val params = mutableMapOf(
            "id" to id,
        )
        if (start != null) {
            params["start"] = start.toString()
        }
        if (end != null) {
            params["end"] = end.toString()
        }
        return request("event", params, GetEventInfoResponse::class.java)
    }

    fun search(query: String, adult: Boolean = false): SearchResponse {
        if (query.isEmpty()) {
            throw IllegalArgumentException("Search query is required.")
        }
        val params = mapOf(
            "query" to query,
            "adult" to adult.toString(),
        )
        return request("search", params, SearchResponse::class.java)
    }

    private fun <T> request(path: String, params: Map<String, String>, returnType: Class<T>): T {
        val url = baseUrl.newBuilder()
            .addPathSegment(path)
            .apply {
                for ((key, value) in params) {
                    addQueryParameter(key, value)
                }
            }
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .headers(Headers.headersOf(
                "apikey", apiKey,
                "User-Agent", "HolidayApiJava/1.0.0", // TODO auto version?
                "X-Platform-Version", System.getProperty("java.version"),
            ))
            .build()
        client.newCall(request).execute().use { response ->
            var data: Map<String, Any> = mapOf()
            try {
                val body = response.body!!.string()
                data = mapper.readValue<Map<String, Any>>(body).toMutableMap()
                data["rateLimit"] = mapOf(
                    "limitMonth" to response.header("X-RateLimit-Limit-Month", "0")!!.toInt(),
                    "remainingMonth" to response.header("X-RateLimit-Remaining-Month", "0")!!.toInt(),
                )
                return mapper.convertValue(data, returnType)
            } catch (e: Exception) {
                if (response.isSuccessful) {
                    throw RuntimeException("Unable to parse response.")
                } else {
                    val error = data.getOrDefault("error", response.message).toString()
                    throw RuntimeException(error)
                }
            }
        }
    }
}