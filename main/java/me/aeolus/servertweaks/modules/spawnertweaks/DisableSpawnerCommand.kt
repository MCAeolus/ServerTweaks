package me.aeolus.servertweaks.modules.spawnertweaks

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DisableSpawnerCommand : CommandExecutor {

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if(s is Player) {

            val blockLooking = s.getTargetBlock(15)

            if(blockLooking != null && blockLooking.type == Material.SPAWNER) {

                if(SpawnerTweaks.isStackedSpawner(blockLooking)) {

                    SpawnerTweaks.getStackerEntity(blockLooking)?.remove()
                    SpawnerTweaks.getStackingMob(blockLooking)?.remove()

                    s.sendMessage("${ChatColor.GREEN}The target spawner has been disabled.")

                } else s.sendMessage("${ChatColor.RED}This spawner has already been disabled!")

            } else s.sendMessage("${ChatColor.RED}You aren't targeting a spawner!")


        }


        return true
    }
}