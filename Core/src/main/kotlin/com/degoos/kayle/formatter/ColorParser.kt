package com.degoos.kayle.formatter

import java.awt.Color

/**
 * Utility for parsing and manipulating colors.
 */
internal object ColorParser {
    /**
     * Parses a color argument which can be either a named color or a hex color.
     */
    fun parseColorArg(arg: String?): Color? {
        if (arg == null) return null
        return NamedColors.get(arg) ?: parseHexColor(arg)
    }

    /**
     * Parses a gradient argument containing multiple colors separated by colons.
     * Example: "red:yellow:green"
     */
    fun parseGradientColors(arg: String): List<Color> {
        return arg.split(":")
            .mapNotNull { parseColorArg(it) }
    }

    /**
     * Parses a hex color string (with or without #).
     * Example: "#FF5733" or "FF5733"
     */
    fun parseHexColor(hex: String): Color? = runCatching {
        val clean = hex.removePrefix("#")
        if (clean.length == 6) {
            Color(
                clean.substring(0, 2).toInt(16),
                clean.substring(2, 4).toInt(16),
                clean.substring(4, 6).toInt(16)
            )
        } else {
            null
        }
    }.getOrNull()

    /**
     * Interpolates between multiple colors based on progress [0, 1].
     */
    fun interpolateColor(colors: List<Color>, progress: Float): Color {
        require(colors.size >= 2) { "At least 2 colors required for interpolation" }

        val clampedProgress = progress.coerceIn(0f, 1f)
        val scaledProgress = clampedProgress * (colors.size - 1)
        val index = scaledProgress.toInt().coerceAtMost(colors.size - 2)
        val localProgress = scaledProgress - index

        val c1 = colors[index]
        val c2 = colors[index + 1]

        return Color(
            (c1.red + (c2.red - c1.red) * localProgress).toInt(),
            (c1.green + (c2.green - c1.green) * localProgress).toInt(),
            (c1.blue + (c2.blue - c1.blue) * localProgress).toInt()
        )
    }
}
