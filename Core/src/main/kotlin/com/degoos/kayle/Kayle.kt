package com.degoos.kayle

import com.degoos.kayle.coroutine.HytaleDispatchers
import com.degoos.kayle.dsl.registerGlobal
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent

@Suppress("unused")
class Kayle(init: JavaPluginInit) : JavaPlugin(init) {


    override fun start() {

        eventRegistry.registerGlobal { it: RemoveWorldEvent ->
            HytaleDispatchers.removeWorldDispatcher(it.world.worldConfig.uuid)
        }
    }

}