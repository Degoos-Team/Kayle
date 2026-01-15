package com.degoos.kayle

import com.degoos.kayle.coroutine.HytaleDispatchers
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent

class Kayle(init: JavaPluginInit) : JavaPlugin(init) {


    override fun start() {

        eventRegistry.register(RemoveWorldEvent::class.java) {
            HytaleDispatchers.removeWorldDispatcher(it.world.worldConfig.uuid)
        }


    }

}