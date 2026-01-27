package com.degoos.kayle.examples

import com.degoos.kayle.Kayle
import com.degoos.kayle.KotlinPlugin
import com.degoos.kayle.asset.AvatarAssetProvider
import com.degoos.kayle.examples.command.KayleCommand
import com.degoos.kayle.profile.PlayerProfileCache
import com.degoos.kayle.profile.PortraitView
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.CompletableFuture

class KayleExamples(init: JavaPluginInit) : KotlinPlugin(init) {

    override fun start() {
        commandRegistry.registerCommand(KayleCommand())
    }
}