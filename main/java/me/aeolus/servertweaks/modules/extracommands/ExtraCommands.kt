package me.aeolus.servertweaks.modules.extracommands

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module

class ExtraCommands : Module {


    override fun create() {

        ServerTweaks.plugin!!.getCommand("discord")!!.setExecutor(DiscordCommand())

    }

    override fun close() {

        ServerTweaks.plugin!!.getCommand("discord")!!.setExecutor(null)

    }


}