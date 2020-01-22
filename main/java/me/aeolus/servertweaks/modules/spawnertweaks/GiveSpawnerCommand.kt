package me.aeolus.servertweaks.modules.spawnertweaks

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException

class GiveSpawnerCommand : CommandExecutor {

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if(s is Player) {

            if(args.isNotEmpty()) {

                when(args[0].toLowerCase()) {

                    "to" -> {
                        //TODO
                    }
                    else -> {

                        try {

                            val parsedArg = EntityType.valueOf(args[0].toUpperCase())
                            var stacks = 1

                            if(args.size > 1) stacks = args[1].toInt()

                            s.inventory.addItem(SpawnerTweaks.createStackedSpawnerItem(parsedArg, stacks))
                            s.sendMessage("${ChatColor.GREEN}You have been given a ${parsedArg.name} spawner!")

                        } catch( e : IllegalArgumentException) {

                            s.sendMessage("${ChatColor.RED}Unknown spawner type '${args[0]}'.")

                        }


                    }

                }

            }

        }

        return true
    }
}