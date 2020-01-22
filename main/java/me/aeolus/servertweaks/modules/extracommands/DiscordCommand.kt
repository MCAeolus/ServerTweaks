package me.aeolus.servertweaks.modules.extracommands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DiscordCommand : CommandExecutor {


    override fun onCommand(s: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {

        if(s is Player) {

            s.sendMessage("${ChatColor.GREEN}Join out Discord Server today by clicking the invite link below!")
            s.sendMessage("${ChatColor.WHITE}${ChatColor.BOLD}https://discord.gg/gxQYyGm")

            //TODO more descriptive

        }


        return true
    }
}