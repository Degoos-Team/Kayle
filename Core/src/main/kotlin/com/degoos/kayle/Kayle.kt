package com.degoos.kayle

import com.degoos.kayle.coroutine.HytaleDispatchers
import com.degoos.kayle.dsl.registerGlobal
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent

@Suppress("unused")
class Kayle(init: JavaPluginInit) : KotlinPlugin(init) {

    init {
        instance = this
    }

    override fun start() {
        eventRegistry.registerGlobal { it: RemoveWorldEvent ->
            HytaleDispatchers.removeWorldDispatcher(it.world.worldConfig.uuid)
        }
    }


    companion object {
        lateinit var instance: Kayle
            private set
    }
}