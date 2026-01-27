package com.degoos.kayle.examples.command

import com.degoos.kayle.Kayle
import com.degoos.kayle.asset.AvatarAssetProvider
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
import com.degoos.kayle.profile.PlayerProfileCache
import com.degoos.kayle.profile.PortraitView
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class KaylePlayerAvatar : AbstractCommand("avatar", "Loads a player avatar and saves it as an asset.") {
    val username = withRequiredArg("name", "The name of the user", ArgTypes.STRING)

    val view = withDefaultArg("view", "The avatar camera view", ArgTypes.STRING, "avatar", null)

    override fun execute(context: CommandContext): CompletableFuture<Void?>? {
        return username.get(context)?.let { name ->
            Kayle.instance.launch {
                val profile = PlayerProfileCache.fetch(name)
                val viewName = view.get(context)
                val view = try {
                    PortraitView.valueOf(viewName.uppercase())
                } catch (_: IllegalArgumentException) {
                    context.sendMessage(Message.raw("Invalid view: $viewName"))
                    return@launch
                }


                if (profile == null) {
                    context.sendMessage(Message.raw("Profile not found."))
                    return@launch
                }

                context.sendMessage(Message.raw("Name: ${profile.username}"))
                context.sendMessage(Message.raw("UUID: ${profile.uuid}"))
                AvatarAssetProvider.fetch(profile.uuid, view)?.let {
                    context.sendMessage(Message.raw("Avatar saved in asset: $it"))

                    if (context.isPlayer) {
                        openGUI(context, it)
                    }

                }

            }.asCompletableFuture().thenRun { }
        }
    }

    private fun openGUI(context: CommandContext, path: String) {
        val player = context.senderAs(Player::class.java)
        val ref = player.reference ?: return
        Kayle.instance.launch(ref.world.dispatcher) {
            val playerRef = ref.store.getComponent(ref, PlayerRef.getComponentType()) ?: return@launch
            player.pageManager.openCustomPage(ref, ref.store, ImagePage(playerRef, path))
        }
    }
}