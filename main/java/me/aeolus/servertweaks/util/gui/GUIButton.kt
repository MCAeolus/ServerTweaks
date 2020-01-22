package me.aeolus.servertweaks.util.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class GUIButton(private var icon : ItemStack, val shouldCancel : Boolean) {


    fun updateIcon(item : ItemStack) {
        icon = item
    }

    fun getIcon() = icon

    abstract fun onClick(event : InventoryClickEvent, session : GUISession)

}