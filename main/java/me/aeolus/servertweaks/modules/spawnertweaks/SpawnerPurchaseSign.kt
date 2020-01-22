package me.aeolus.servertweaks.modules.spawnertweaks

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.util.StringUtil
import org.bukkit.ChatColor
import org.bukkit.block.Sign
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.lang.Exception
import java.util.logging.Level
import java.util.logging.Level.INFO

class SpawnerPurchaseSign : Listener {

    companion object {

        val PURCHASE_HEADER = "${ChatColor.BLUE}[Buy]"

    }


    @EventHandler
    fun createPurchaseSign(event : SignChangeEvent) {

        if(event.lines[0] == PURCHASE_HEADER) event.isCancelled = true

        if(event.lines[0].toLowerCase() == "[spawner]" && event.player.hasPermission("server.management")) {

            val ent : EntityType
            val cost: Int

            try {

                ent = EntityType.valueOf(event.lines[1].trim().toUpperCase().replace(" ", "_"))

            } catch ( e : Exception) {

                event.player.sendMessage("${ChatColor.RED}Invalid entity type!")
                event.block.breakNaturally()
                return

            }

            try {

                cost = event.lines[2].replace("$", "").trim().toInt()

            } catch( e : Exception) {

                event.player.sendMessage("${ChatColor.RED}Invalid price set!")
                event.block.breakNaturally()
                return

            }

            event.setLine(0, PURCHASE_HEADER)
            event.setLine(1, StringUtil.romanCapitilizationSentence(ent.name.replace("_", " ")))
            event.setLine(2, "$$cost")

            event.player.sendMessage("${ChatColor.GREEN}Successfully created spawner purchase sign for entity ${ent.name} and cost $cost!")

        }
    }

    @EventHandler
    fun interactSignEvent(event : PlayerInteractEvent) {

        if(event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock!!.state is Sign) {

            val sign = event.clickedBlock!!.state as Sign

            if(sign.lines[0] == PURCHASE_HEADER) {

                event.isCancelled = true

                val ent = EntityType.valueOf(sign.lines[1].replace(" ", "_").toUpperCase())

                val cost = sign.lines[2].replace("$", "").toInt()

                if(ServerTweaks.economyAPI!!.has(event.player, cost.toDouble())) {

                    val spawner = SpawnerTweaks.createStackedSpawnerItem(ent, 1)

                    val retAdd = event.player.inventory.addItem(spawner)

                    if(retAdd.isEmpty()) {

                        ServerTweaks.economyAPI!!.withdrawPlayer(event.player, cost.toDouble())

                        event.player.sendMessage("${ChatColor.GREEN}You bought a ${sign.lines[1].toLowerCase()} spawner!")

                    } else
                        event.player.sendMessage("${ChatColor.RED}You need to make space in your inventory first!")


                } else
                    event.player.sendMessage("${ChatColor.RED}You don't have the funds to buy this spawner!")

            }


        }

    }

}