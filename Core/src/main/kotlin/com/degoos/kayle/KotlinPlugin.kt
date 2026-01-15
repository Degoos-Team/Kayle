package com.degoos.kayle

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

open class KotlinPlugin(init: JavaPluginInit) : JavaPlugin(init), CoroutineScope {

    private val supervisor = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = supervisor // + HytaleDispatchers.Async


    override fun shutdown() {
        supervisor.cancel(CancellationException("Plugin shutdown"))
        super.shutdown()
    }

}