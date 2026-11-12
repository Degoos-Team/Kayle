package com.degoos.kayle.extension

import com.degoos.kayle.coroutine.HytaleDispatchers
import com.hypixel.hytale.protocol.packets.interface_.EventTitleStyle
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.util.EventTitleUtil
import kotlinx.coroutines.CoroutineDispatcher

val World.dispatcher: CoroutineDispatcher get() = HytaleDispatchers.forWorld(this)

val World.uuid get() = worldConfig.uuid

/**
 * Broadcasts a title and optional subtitle to all players inside the world.
 * This function is thread-safe.
 *
 * @param title The primary title message to display to the player.
 * @param subtitle The secondary subtitle message to display. Defaults to an empty message.
 * @param style The visual style of the title. Defaults to [EventTitleStyle.Default].
 * @param icon An optional icon to display alongside the title. Defaults to null.
 * @param duration The total duration (in seconds) for which the title is displayed. Defaults to 4.0 seconds.
 * @param fadeInDuration The duration (in seconds) for the fade-in animation of the title. Defaults to 1.5 seconds.
 * @param fadeOutDuration The duration (in seconds) for the fade-out animation of the title. Defaults to 1.5 seconds.
 */
fun World.broadcastTitle(
    title: Message,
    subtitle: Message = Message.empty(),
    style: EventTitleStyle = EventTitleStyle.Default,
    icon: String? = null,
    duration: Float = 4.0f,
    fadeInDuration: Float = 1.5f,
    fadeOutDuration: Float = 1.5f
) {
    val show = {
        EventTitleUtil.showEventTitleToWorld(
            title,
            subtitle,
            style,
            icon,
            duration,
            fadeInDuration,
            fadeOutDuration,
            this
        )
    }
    if (isInThread) show() else execute(show)
}

/**
 * Broadcasts a title using the legacy major/default flag.
 */
@Deprecated(
    "Hytale replaced the isMajor flag with EventTitleStyle.",
    ReplaceWith(
        "broadcastTitle(title, subtitle, if (isMajor) EventTitleStyle.Major else EventTitleStyle.Default, " +
            "icon, duration, fadeInDuration, fadeOutDuration)",
        "com.hypixel.hytale.protocol.packets.interface_.EventTitleStyle"
    )
)
fun World.broadcastTitle(
    title: Message,
    subtitle: Message,
    isMajor: Boolean,
    icon: String? = null,
    duration: Float = 4.0f,
    fadeInDuration: Float = 1.5f,
    fadeOutDuration: Float = 1.5f
) = broadcastTitle(
    title,
    subtitle,
    if (isMajor) EventTitleStyle.Major else EventTitleStyle.Default,
    icon,
    duration,
    fadeInDuration,
    fadeOutDuration
)
