package com.degoos.kayle

import com.hypixel.hytale.server.core.auth.SessionServiceClient
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

open class KotlinPlugin(init: JavaPluginInit) : JavaPlugin(init), CoroutineScope {

    private val supervisor = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = supervisor + Dispatchers.Default


    override fun shutdown() {
        supervisor.cancel(CancellationException("Plugin shutdown"))
        super.shutdown()
    }

}