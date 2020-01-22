package me.aeolus.servertweaks.modules.headbounty

import me.aeolus.servertweaks.ServerTweaks
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugGiveHeadCommand : CommandExecutor {

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if ( s is Player ) {

            var player : OfflinePlayer = s

            if (args.isNotEmpty())
                player = ServerTweaks.plugin!!.server.getOfflinePlayer(args[0])

            val bounty = HeadBounty.calculateBounty(player)

            val head = HeadBounty.makeHeadBounty(player, bounty)

            s.inventory.addItem(head)

        }

        return true
    }
}