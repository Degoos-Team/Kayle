package com.degoos.kayle.formatter

import java.awt.Color

/**
 * Represents the current styling state during message parsing.
 * Immutable state object following the builder pattern.
 */
internal data class StyleState(
    val color: Color? = null,
    val gradient: List<Color>? = null,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underlined: Boolean = false,
    val monospace: Boolean = false,
    val link: String? = null
) {
    fun withColor(color: Color?): StyleState =
        copy(color = color, gradient = null)

    fun withGradient(gradient: List<Color>): StyleState =
        copy(color = null, gradient = gradient)

    fun withBold(bold: Boolean): StyleState =
        copy(bold = bold)

    fun withItalic(italic: Boolean): StyleState =
        copy(italic = italic)

    fun withUnderlined(underlined: Boolean): StyleState =
        copy(underlined = underlined)

    fun withMonospace(monospace: Boolean): StyleState =
        copy(monospace = monospace)

    fun withLink(link: String?): StyleState =
        copy(link = link)
}
