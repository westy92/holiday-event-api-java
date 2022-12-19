package model

/**
 * The Response returned by getEventInfo
 */
data class GetEventInfoResponse(
    /**
     * The Event Info
     */
    val event: EventInfo,
    override val rateLimit: RateLimit,
): StandardResponse
