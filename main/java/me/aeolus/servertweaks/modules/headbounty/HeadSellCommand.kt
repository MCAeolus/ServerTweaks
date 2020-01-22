package me.aeolus.servertweaks.modules.headbounty

import me.aeolus.servertweaks.ServerTweaks
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class HeadSellCommand : CommandExecutor {


    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {
        if(s is Player) {

            val hand = s.inventory.itemInMainHand





            val handMeta = hand.itemMeta

            if(handMeta != null) {




                if (handMeta.hasLore()) {
                    val lore = handMeta.lore!!

                    if(lore[1]?.contains("Worth") == true) {
                        val bountyPlayer = ServerTweaks.plugin!!.server.getPlayer(UUID.fromString(lore[0]))

                        val bountyCost = ServerTweaks.economyAPI!!.getBalance(bountyPlayer) * HeadBounty.PERCENTAGE_LOST_ON_DEATH

                        ServerTweaks.economyAPI!!.withdrawPlayer(bountyPlayer, bountyCost)
                        bountyPlayer!!.sendMessage("${ChatColor.RED}You lost your head (and ${ChatColor.GREEN}$$bountyCost${ChatColor.RED}) to ${ChatColor.GOLD}${s.displayName}${ChatColor.RED}.")

                        ServerTweaks.economyAPI!!.depositPlayer(s, bountyCost)
                        s.sendMessage("${ChatColor.GREEN}You sold ${ChatColor.WHITE}${bountyPlayer.displayName}'s ${ChatColor.GREEN}head for ${ChatColor.GOLD}$$bountyCost${ChatColor.GREEN}.")

                        s.inventory.itemInMainHand.type = Material.AIR

                        return true
                    }
                }
            }
            s.sendMessage("Invalid item in main hand.")
            return true

        }
        s.sendMessage("You aren't holding anything!")

        return true
    }
}