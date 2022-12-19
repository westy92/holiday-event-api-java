package model

/**
 * The API's standard response
 */
interface StandardResponse {
    /**
     * The API plan's current rate limit and status
     */
    val rateLimit: RateLimit
}
