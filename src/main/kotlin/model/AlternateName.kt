package model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Information about an Event's Alternate Name
 */
data class AlternateName(
    /**
     * An Event's Alternate Name
     */
    val name: String,
    /**
     * The first year this Alternate Name was in effect (null implies none or unknown)
     */
    @JsonProperty("first_year")
    val firstYear: Int?,
    /**
     * The last year this Alternate Name was in effect (null implies none or unknown)
     */
    @JsonProperty("last_year")
    val lastYear: Int?,
)
