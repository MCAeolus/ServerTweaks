package me.aeolus.servertweaks.modules.disableitems

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class DisableItems : Module, Listener {

    companion object {

        val BLOCK_BLACKLIST = listOf(Material.ENCHANTING_TABLE, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.GRINDSTONE)
        val DISABLED_ITEM_LORE = listOf("${ChatColor.DARK_RED}${ChatColor.BOLD}WARNING: ${ChatColor.RED}This block can't be used!", "${ChatColor.RED}Only make this if you want to use it as decoration.")

    }

    override fun create() {

        ServerTweaks.plugin!!.registerAsListener(this)

    }

    override fun close() {

        ServerTweaks.plugin!!.unregisterListener(this)

    }

    @EventHandler
    fun interact(event : PlayerInteractEvent) {

        if(event.clickedBlock != null) {

            if(BLOCK_BLACKLIST.contains(event.clickedBlock!!.type) && event.action == Action.RIGHT_CLICK_BLOCK) {

                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}This item has no use!")

            }

        }

    }

    @EventHandler
    fun crafting(event : CraftItemEvent) {

        if(BLOCK_BLACKLIST.contains(event.recipe.result.type)) {

            val result = event.recipe.result
            val resultMeta = result.itemMeta
            resultMeta.lore = DISABLED_ITEM_LORE
            result.itemMeta = resultMeta

        }

    }

    @EventHandler
    fun sayNoToElytra(event : PlayerInteractEntityEvent) {

        if(event.rightClicked is ItemFrame) {

            val frameItem = (event.rightClicked as ItemFrame).item

            if(frameItem.type == Material.ELYTRA) {
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}You can't seem to pry the elytra out of its holding frame!")
            }

        }

    }

    @EventHandler
    fun sayNoToElytra2(event : EntityDamageByEntityEvent) {

        if(event.entity is ItemFrame) {

            val frameItem = (event.entity as ItemFrame).item

            if(frameItem.type == Material.ELYTRA) {

                (event.entity as ItemFrame).setItem(ItemStack(Material.AIR))
                if(event.damager is Player)
                    event.damager.sendMessage("${ChatColor.GRAY}The elytra disappear with a puff of smoke.")

                event.entity.world.spawnParticle(Particle.SMOKE_NORMAL, event.entity.location, 1)

            }

        }

    }

    //TODO check toggle glide too?
}