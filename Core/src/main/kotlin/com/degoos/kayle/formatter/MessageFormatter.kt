package com.degoos.kayle.formatter

import com.hypixel.hytale.server.core.Message
import java.util.*
import java.util.regex.Pattern

/**
 * MessageFormatter provides functionality to parse strings with TinyMsg-style formatting tags
 * and convert them into Hytale Message objects.
 *
 * Supports colors (named and hex), gradients, text styles (bold, italic, underline, monospace),
 * and links. Tags can be nested for complex formatting.
 */
class MessageFormatter {

    companion object {
        // Matches <tag>, <tag:arg>, </tag>
        private val TAG_PATTERN: Pattern = Pattern.compile("<(/?)([a-zA-Z0-9_]+)(?::([^>]+))?>")

        /**
         * Parses a string containing TinyMsg formatting tags and converts it into a Hytale Message.
         *
         * This method processes all supported tags including colors, gradients, styles, and links.
         * Tags can be nested indefinitely for complex formatting.
         *
         * Supported tags:
         * - Colors: `<color:hex>`, `<c:name>`, named colors as tags (e.g., `<red>`)
         * - Gradients: `<gradient:color1:color2:...>`, `<grnt:hex1:hex2>`
         * - Styles: `<bold>`, `<b>`, `<italic>`, `<i>`, `<em>`, `<underline>`, `<u>`, `<monospace>`, `<mono>`
         * - Links: `<link:url>`, `<url:url>`
         * - Reset: `<reset>`, `<r>`
         * - Closing: `</tag>`
         *
         * @param text The string to parse, containing TinyMsg formatting tags
         * @return A formatted [Message] object ready to be sent to players
         * @see Message
         */
        fun parse(text: String): Message {
            if (!text.contains("<")) {
                return Message.raw(text)
            }

            val root = Message.empty()
            val stateStack: Deque<StyleState> = ArrayDeque()
            stateStack.push(StyleState()) // Start with default empty state

            val matcher = TAG_PATTERN.matcher(text)
            var lastIndex = 0

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()

                // Handle text before this tag
                if (start > lastIndex) {
                    val content = text.substring(lastIndex, start)
                    val segmentMsg = MessageStyler.createStyledMessage(content, stateStack.peek())
                    root.insert(segmentMsg)
                }

                // Process the tag
                val isClosing = matcher.group(1) == "/"
                val tagName = TagProcessor.normalizeTagName(matcher.group(2))
                val tagArg = matcher.group(3)

                if (isClosing) {
                    if (stateStack.size > 1) {
                        stateStack.pop()
                    }
                } else {
                    val currentState = stateStack.peek()
                    val newState = TagProcessor.processOpeningTag(tagName, tagArg, currentState)

                    // Handle reset tag specially - clear the stack
                    if (tagName == "reset" || tagName == "r") {
                        stateStack.clear()
                    }

                    stateStack.push(newState)
                }

                lastIndex = end
            }

            // Handle remaining text
            if (lastIndex < text.length) {
                val content = text.substring(lastIndex)
                val segmentMsg = MessageStyler.createStyledMessage(content, stateStack.peek())
                root.insert(segmentMsg)
            }

            return root
        }
    }
}