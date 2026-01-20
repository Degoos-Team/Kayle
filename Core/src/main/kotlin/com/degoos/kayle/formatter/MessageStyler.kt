package com.degoos.kayle.formatter

import com.hypixel.hytale.protocol.MaybeBool
import com.hypixel.hytale.server.core.Message
import kotlin.math.max

/**
 * Applies gradients and styling to message content.
 */
internal object MessageStyler {
    /**
     * Creates a styled message from content and style state.
     */
    fun createStyledMessage(content: String, state: StyleState): Message {
        return if (!state.gradient.isNullOrEmpty()) {
            applyGradient(content, state)
        } else {
            applySolidStyle(content, state)
        }
    }

    private fun applySolidStyle(content: String, state: StyleState): Message {
        val msg = Message.raw(content)
        applyStyles(msg, state)
        return msg
    }

    private fun applyGradient(text: String, state: StyleState): Message {
        val container = Message.empty()
        val colors = state.gradient ?: return container

        if (colors.isEmpty()) return container

        val length = text.length
        if (length == 0) return container

        text.forEachIndexed { index, ch ->
            val progress = index / max(length - 1, 1).toFloat()
            val color = ColorParser.interpolateColor(colors, progress)

            val charMsg = Message.raw(ch.toString()).color(color)
            applyStyles(charMsg, state)
            container.insert(charMsg)
        }

        return container
    }

    private fun applyStyles(msg: Message, state: StyleState) {
        state.color?.let { msg.color(it) }
        if (state.bold) msg.bold(true)
        if (state.italic) msg.italic(true)
        if (state.monospace) msg.monospace(true)
        if (state.underlined) msg.formattedMessage.underlined = MaybeBool.True
        state.link?.let { msg.link(it) }
    }
}
