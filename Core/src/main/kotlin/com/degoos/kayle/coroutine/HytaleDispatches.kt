package com.degoos.kayle.coroutine

import com.hypixel.hytale.server.core.universe.world.World
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * A custom dispatcher that delegates execution to the Hytale world thread.
 * It schedules the coroutine execution to run on the next tick of the target thread
 * (for example, the main server thread or a specific world thread).
 */
class HytaleTickDispatcher(
    private val scheduler: (Runnable) -> Unit,
    private val threadName: String
) : CoroutineDispatcher() {

    /**
     * Dispatches the execution of a runnable block to the game engine's task queue.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        scheduler(block)
    }

    override fun toString(): String = "HytaleDispatcher($threadName)"
}

object HytaleDispatchers {

    private val worldDispatchers = ConcurrentHashMap<UUID, CoroutineDispatcher>()

    fun forWorld(world: World) = worldDispatchers.computeIfAbsent(world.worldConfig.uuid) {
        HytaleTickDispatcher(
            scheduler = { task ->
                world.execute(task)
            },
            threadName = "World-${world.worldConfig.uuid}"
        )
    }

    fun removeWorldDispatcher(world: UUID) {
        worldDispatchers.remove(world)
    }
}
