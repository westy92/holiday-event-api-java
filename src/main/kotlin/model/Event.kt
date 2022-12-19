package model

/**
 * Information about an Event
 */
interface Event {
    /**
     * The Event Id
     */
    val id: String
    /**
     * The Event Name
     */
    val name: String
    /**
     * The Event URL
     */
    val url: String
}