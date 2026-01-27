package com.degoos.kayle.examples.command

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class KayleShowImageUI : AbstractPlayerCommand(
    "image",
    "Shows an image"
) {
    val path = withRequiredArg("path", "The asset path", ArgTypes.STRING)

    override fun execute(
        context: CommandContext,
        store: Store<EntityStore?>,
        ref: Ref<EntityStore?>,
        playerRef: PlayerRef,
        world: World
    ) {
        val path = path.get(context)

        val player = context.senderAs(Player::class.java)
        player.pageManager.openCustomPage(ref, store, ImagePage(playerRef, path))
    }

}

class ImagePage(player: PlayerRef, val path: String) : CustomUIPage(player, CustomPageLifetime.CanDismiss) {
    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        uiCommandBuilder.append("Pages/Kayle/Image.ui")
        uiCommandBuilder.set("#Image.AssetPath", path)
    }


}