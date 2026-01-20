package com.degoos.kayle.formatter

import java.awt.Color

/**
 * Predefined named colors matching Minecraft color codes.
 */
internal object NamedColors {
    private val colors = mapOf(
        "black" to Color(0, 0, 0),
        "dark_blue" to Color(0, 0, 170),
        "dark_green" to Color(0, 170, 0),
        "dark_aqua" to Color(0, 170, 170),
        "dark_red" to Color(170, 0, 0),
        "dark_purple" to Color(170, 0, 170),
        "gold" to Color(255, 170, 0),
        "gray" to Color(170, 170, 170),
        "dark_gray" to Color(85, 85, 85),
        "blue" to Color(85, 85, 255),
        "green" to Color(85, 255, 85),
        "aqua" to Color(85, 255, 255),
        "red" to Color(255, 85, 85),
        "light_purple" to Color(255, 85, 255),
        "yellow" to Color(255, 255, 85),
        "white" to Color(255, 255, 255)
    )

    fun get(name: String): Color? = colors[name]

    fun contains(name: String): Boolean = colors.containsKey(name)
}
