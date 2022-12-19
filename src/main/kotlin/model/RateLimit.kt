package model

/**
 * Your API plan's current Rate Limit and status. Upgrade to increase these limits.
 */
data class RateLimit(
    /**
     * The amount of requests allowed this month
     */
    val limitMonth: Int,
    /**
     * The amount of requests remaining this month
     */
    val remainingMonth: Int,
)
