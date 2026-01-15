package com.degoos.kayle.examples

import com.degoos.kayle.KotlinPlugin
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.teleport
import com.degoos.kayle.dsl.transform
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class KayleExamples(init: JavaPluginInit) : KotlinPlugin(init) {

    override fun start() {
        println("Hello!")

        eventRegistry.registerGlobal(AddPlayerToWorldEvent::class.java) { event ->
            val world = event.world
            val player = event.holder.getComponent(PlayerRef.getComponentType()) ?: return@registerGlobal
            launch(world.dispatcher) {
                delay(5000L)
                val pos = player.transform.position.toVector3i()
                world.setBlock(pos.x, pos.y, pos.z, "Bench_Arcane");
            }
        }

        commandRegistry.registerCommand(object : AbstractCommand("kayle", "Kayle example") {
            override fun execute(context: CommandContext): CompletableFuture<Void?>? {

                context.senderAsPlayerRef()?.let { ref ->
                    launch(ref.world.dispatcher) {
                        val transform = ref.transform ?: return@launch
                        transform.position.y += 10
                        ref.teleport(transform)
                    }
                }
                return null
            }

        })
    }

}