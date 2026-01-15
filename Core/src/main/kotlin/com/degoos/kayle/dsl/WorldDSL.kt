package com.degoos.kayle.dsl

import com.degoos.kayle.coroutine.HytaleDispatchers
import com.hypixel.hytale.server.core.universe.world.World
import kotlinx.coroutines.CoroutineDispatcher

val World.dispatcher: CoroutineDispatcher get() = HytaleDispatchers.forWorld(this)