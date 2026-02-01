package com.degoos.kayle.extension

import com.degoos.kayle.formatter.MessageFormatter
import com.hypixel.hytale.server.core.Message


/**
 * Parses the message containing TinyMsg formatting tags.
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
 * @return A formatted [Message] object ready to be sent to players
 * @see Message
 * @see MessageFormatter
 */
fun Message.parseTags(): Message = MessageFormatter.parse(this.ansiMessage)