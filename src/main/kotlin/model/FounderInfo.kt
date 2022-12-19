package model

/**
 * Information about an Event Founder
 */
data class FounderInfo(
    /**
     * The Founder's name
     */
    val name: String,
    /**
     * A link to the Founder
     */
    val url: String?,
    /**
     * The date the Event was founded
     */
    val date: String?,
)
