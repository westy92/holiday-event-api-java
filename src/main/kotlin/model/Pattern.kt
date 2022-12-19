package model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Information about an Event's Pattern
 */
data class Pattern(
    /**
     * The first year this event is observed (null implies none or unknown)
     */
    @JsonProperty("first_year")
    val firstYear: Int?,
    /**
     * The last year this event is observed (null implies none or unknown)
     */
    @JsonProperty("last_year")
    val lastYear: Int?,
    /**
     * A description of how this event is observed (formatted as plain text)
     */
    val observed: String,
    /**
     * A description of how this event is observed (formatted as HTML)
     */
    @JsonProperty("observed_html")
    val observedHtml: String,
    /**
     * A description of how this event is observed (formatted as Markdown)
     */
    @JsonProperty("observed_markdown")
    val observedMarkdown: String,
    /**
     * For how many days this event is celebrated
     */
    val length: Int,
)
