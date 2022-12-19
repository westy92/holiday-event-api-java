package model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The Response returned by getEvents
 */
data class GetEventsResponse(
    /**
     * Whether Adult entries can be included
     */
    val adult: Boolean,
    /**
     * The Date string
     */
    val date: String,
    /**
     * The Timezone used to calculate the Date's Events
     */
    val timezone: String,
    /**
     * The Date's Events
     */
    val events: List<EventSummary>,
    /**
     * Multi-day Events that start on Date
     */
    @JsonProperty("multiday_starting")
    val multidayStarting: List<EventSummary>?,
    /**
     * Multi-day Events that are continuing their observance on Date
     */
    @JsonProperty("multiday_ongoing")
    val multidayOngoing: List<EventSummary>?,
    override val rateLimit: RateLimit,
): StandardResponse
