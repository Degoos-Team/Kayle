package com.degoos.kayle.examples

import com.degoos.kayle.Kayle
import com.degoos.kayle.KotlinPlugin
import com.degoos.kayle.profile.PlayerProfileCache
import com.degoos.kayle.profile.PortraitView
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import kotlinx.coroutines.future.future
import java.io.File
import java.util.concurrent.CompletableFuture

class KayleExamples(init: JavaPluginInit) : KotlinPlugin(init) {

    override fun start() {
        println("Hello!")

        //val registry = eventRegistry.registerGlobal(AddPlayerToWorldEvent::class.java) { event ->
        //    val world = event.world
        //    val player = event.holder.getComponent(PlayerRef.getComponentType()) ?: return@registerGlobal
        //    launch(world.dispatcher) {
        //        delay(5000L)
        //        val pos = player.transform.position.toVector3i()
        //        world.setBlock(pos.x, pos.y, pos.z, "Bench_Arcane");
        //    }
        //}

        commandRegistry.registerCommand(object : AbstractCommand("kayle-info", "Kayle example") {


            val username = withRequiredArg("name", "The name of the user", ArgTypes.STRING)


            override fun execute(context: CommandContext): CompletableFuture<Void?>? {
                return username.get(context)?.let { name ->
                    Kayle.instance.future {
                        val profile = PlayerProfileCache.fetch(name)

                        if (profile == null) {
                            context.sendMessage(Message.raw("Profile not found."))
                            return@future null
                        }
                        context.sendMessage(Message.raw("Name: ${profile.username}"))
                        context.sendMessage(Message.raw("UUID: ${profile.uuid}"))
                        profile.skin?.let { skin ->
                            context.sendMessage(Message.raw("Body char.: ${skin.bodyCharacteristic}"))
                            context.sendMessage(Message.raw("Underwear: ${skin.underwear}"))
                            context.sendMessage(Message.raw("Face: ${skin.face}"))
                            context.sendMessage(Message.raw("Eyes: ${skin.eyes}"))
                            context.sendMessage(Message.raw("Ears: ${skin.ears}"))
                            context.sendMessage(Message.raw("Mouth: ${skin.mouth}"))
                            context.sendMessage(Message.raw("Facial hair: ${skin.facialHair}"))
                            context.sendMessage(Message.raw("Haircut: ${skin.haircut}"))
                            context.sendMessage(Message.raw("Eyebrows: ${skin.eyebrows}"))
                            context.sendMessage(Message.raw("Pants: ${skin.pants}"))
                            context.sendMessage(Message.raw("Overpants: ${skin.overpants}"))
                            context.sendMessage(Message.raw("Undertop: ${skin.undertop}"))
                            context.sendMessage(Message.raw("Overtop: ${skin.overtop}"))
                            context.sendMessage(Message.raw("Shoes: ${skin.shoes}"))
                            context.sendMessage(Message.raw("Head acc.: ${skin.headAccessory}"))
                            context.sendMessage(Message.raw("Face acc.: ${skin.faceAccessory}"))
                            context.sendMessage(Message.raw("Ear acc.: ${skin.earAccessory}"))
                            context.sendMessage(Message.raw("Skin feat.: ${skin.skinFeature}"))
                            context.sendMessage(Message.raw("Gloves: ${skin.gloves}"))
                            context.sendMessage(Message.raw("Cape: ${skin.cape}"))
                        }

                        val file = File("avatar.png")
                        profile.avatarService.fetch(PortraitView.AVATAR)?.let { file.writeBytes(it) }
                        profile
                    }.thenRun { }
                }
            }

        })
    }

}