package com.degoos.kayle.examples.command

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class KayleCommand : AbstractCommandCollection("kayle", "Kayle examples") {

    init {
        addSubCommand(KaylePlayerInfo())
        addSubCommand(KaylePlayerAvatar())
        addSubCommand(KayleShowImageUI())
    }

}