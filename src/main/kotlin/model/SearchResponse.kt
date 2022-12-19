package model

/**
 * The Response returned by search
 */
data class SearchResponse(
    /**
     * The search query
     */
    val query: String,
    /**
     * Whether Adult entries can be included
     */
    val adult: Boolean,
    /**
     * The found Events
     */
    val events: List<EventSummary>,
)
