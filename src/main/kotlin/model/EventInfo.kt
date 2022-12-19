package model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Information about an Event
 */
data class EventInfo(
    override val id: String,
    override val name: String,
    override val url: String,
    /**
     * Whether this Event is unsafe for children or viewing at work
     */
    val adult: Boolean,
    /**
     * The Event's Alternate Names
     */
    @JsonProperty("alternate_names")
    val alternateNames: List<AlternateName>,
    /**
     * The Event's hashtags
     */
    val hashtags: List<String>?,
    /**
     * The Event's images
     */
    val image: ImageInfo?,
    /**
     * The Event's sources
     */
    val sources: List<String>?,
    /**
     * The Event's description
     */
    val description: RichText?,
    /**
     * How to observe the Event
     */
    @JsonProperty("how_to_observe")
    val howToObserve: RichText?,
    /**
     * Patterns defining when the Event is observed
     */
    val patterns: List<Pattern>?,
    /**
     * The Event's founders
     */
    val founders: List<FounderInfo>?,
    /**
     * The Event Occurrences (when it occurs)
     */
    val occurrences: List<Occurrence>?,
) : Event
