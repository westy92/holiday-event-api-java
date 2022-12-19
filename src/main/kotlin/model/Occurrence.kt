package model

/**
 * Information about an Event's Occurrence
 */
data class Occurrence(
    /**
     * The date or timestamp the Event occurs
     */
    val date: String,
    /**
     * The length (in days) of the Event occurrence
     */
    val length: Int,
)
