package me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUISession
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ButtonCurrentEconomy(p : Player) : GUIButton(makeItem(p),true) {


    companion object {

        fun makeItem(p : Player) : ItemStack {

            val econ = ServerTweaks.economyAPI!!.getBalance(p).toInt()

            val econItem = ItemStack(Material.GOLD_INGOT)
            val econMeta = econItem.itemMeta

            econMeta.setDisplayName("${ChatColor.GOLD}${ChatColor.BOLD}Current Balance")
            econMeta.lore = listOf("${ChatColor.WHITE}You currently have ${ChatColor.GREEN}$$econ${ChatColor.WHITE}.")

            econItem.itemMeta = econMeta


            return econItem

        }

    }

    override fun onClick(event: InventoryClickEvent, session: GUISession) {

    }
}