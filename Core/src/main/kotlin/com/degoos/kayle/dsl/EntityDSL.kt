package com.degoos.kayle.dsl

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

fun Ref<EntityStore>.teleport(
    position: Vector3d,
    rotation: Vector3f = Vector3f.ZERO,
    world: World? = null
) {
    val teleport = Teleport(world, position, rotation)
    store.putComponent(this, Teleport.getComponentType(), teleport)
}

fun Ref<EntityStore>.teleport(
    transform: Transform
) {
    val teleport = Teleport(transform)
    store.putComponent(this, Teleport.getComponentType(), teleport)
}

val Ref<EntityStore>.transform get() = store.getComponent(this, TransformComponent.getComponentType())?.transform

val Ref<EntityStore>.world get() = store.externalData.world