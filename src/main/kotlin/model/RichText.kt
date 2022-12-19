package model

/**
 * Formatted Text
 */
data class RichText(
    /**
     * Formatted as plain text
     */
    val text: String?,
    /**
     * Formatted as HTML
     */
    val html: String?,
    /**
     * Formatted as Markdown
     */
    val markdown: String?,
)
