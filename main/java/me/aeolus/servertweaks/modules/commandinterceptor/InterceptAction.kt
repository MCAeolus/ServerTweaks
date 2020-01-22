package me.aeolus.servertweaks.modules.commandinterceptor

import org.bukkit.event.player.PlayerCommandPreprocessEvent

abstract class InterceptAction(val cancel : Boolean) {

    abstract fun onIntercept(event : PlayerCommandPreprocessEvent)

}