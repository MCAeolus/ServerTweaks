package me.aeolus.servertweaks.modules.commandinterceptor

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.java.JavaPlugin

class CommandInterceptor : Module, Listener {

    companion object {

        private val commandMap = HashMap<List<String>, InterceptAction>()

        fun addCommand(primaryName : String, interceptObject : InterceptAction) {

            val commandList = ArrayList<String>()
            commandList.add(primaryName)

            for ( p in ServerTweaks.plugin!!.server.pluginManager.plugins) {

                if(p.description.commands.containsKey(primaryName)) {

                    commandList.addAll((p as JavaPlugin).getCommand(primaryName)!!.aliases)
                    commandMap[commandList] = interceptObject

                    return

                }

            }

            ServerTweaks.plugin!!.logger.warning("Could not find command '$primaryName' throughout plugin directory.")
        }

    }



    override fun create() {

        ServerTweaks.plugin!!.registerAsListener(this)

        addCommand("plugins", object : InterceptAction(true){

            override fun onIntercept(event: PlayerCommandPreprocessEvent) {
                event.player.sendMessage("${ChatColor.WHITE}Plugins (1): ${ChatColor.GREEN}ServerTweaks by Aeolus")
            }

        })

    }

    override fun close() {

        ServerTweaks.plugin!!.unregisterListener(this)

    }

    @EventHandler
    fun interceptCommand(event : PlayerCommandPreprocessEvent) {

        event.player.sendMessage(event.message)

        for (c in commandMap )
            if(c.key.contains(event.message.replace("/", "").toLowerCase())) {
                val intEvent = c.value

                event.isCancelled = intEvent.cancel
                intEvent.onIntercept(event)

                return
            }

    }

}
