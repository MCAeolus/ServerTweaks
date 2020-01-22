package me.aeolus.servertweaks.util.gui.genericButtons

import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUISession
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class StationaryButton(icon : ItemStack) : GUIButton(icon, true) {

    override fun onClick(event: InventoryClickEvent, session: GUISession) {

        //do nothing

    }
}