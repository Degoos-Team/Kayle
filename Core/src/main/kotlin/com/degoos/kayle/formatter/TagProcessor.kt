package com.degoos.kayle.formatter

import java.util.*

/**
 * Processes formatting tags and updates style state accordingly.
 */
internal object TagProcessor {
    /**
     * Processes an opening tag and returns the new style state.
     */
    fun processOpeningTag(
        tagName: String,
        tagArg: String?,
        currentState: StyleState
    ): StyleState {
        // Check if it's a named color tag
        if (NamedColors.contains(tagName)) {
            return currentState.withColor(NamedColors.get(tagName))
        }

        return when (tagName) {
            "color", "c", "colour" -> {
                ColorParser.parseColorArg(tagArg)?.let { currentState.withColor(it) }
                    ?: currentState
            }

            "grnt", "gradient" -> {
                if (tagArg != null) {
                    val colors = ColorParser.parseGradientColors(tagArg)
                    if (colors.isNotEmpty()) {
                        currentState.withGradient(colors)
                    } else {
                        currentState
                    }
                } else {
                    currentState
                }
            }

            "bold", "b" -> currentState.withBold(true)
            "italic", "i", "em" -> currentState.withItalic(true)
            "underline", "u" -> currentState.withUnderlined(true)
            "monospace", "mono" -> currentState.withMonospace(true)
            "link", "url" -> tagArg?.let { currentState.withLink(it) } ?: currentState
            "reset", "r" -> StyleState() // Reset to default state

            else -> currentState // Unknown tag, ignore
        }
    }

    /**
     * Normalizes a tag name to lowercase for consistent comparison.
     */
    fun normalizeTagName(tagName: String): String {
        return tagName.lowercase(Locale.getDefault())
    }
}
