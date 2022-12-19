package model

/**
 * A summary of an Event
 */
data class EventSummary(
    override val id: String,
    override val name: String,
    override val url: String,
): Event
