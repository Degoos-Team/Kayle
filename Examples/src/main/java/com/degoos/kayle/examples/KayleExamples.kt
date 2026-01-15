package com.degoos.kayle.examples

import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit

class KayleExamples(init: JavaPluginInit) : KotlinPlugin(init) {

    override fun start() {
        println("Hello!")
    }

}