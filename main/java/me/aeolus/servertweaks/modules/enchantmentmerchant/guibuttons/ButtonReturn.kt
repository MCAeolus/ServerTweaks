package me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons

import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant
import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUISession
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ButtonReturn : GUIButton(denyButton, true) {

    companion object {

        private val denyButton : ItemStack = ItemStack(Material.RED_STAINED_GLASS)

        init {
            val denyMeta = denyButton.itemMeta
            denyMeta.setDisplayName("${ChatColor.RED}Go Back")
            denyButton.itemMeta = denyMeta

        }
    }




    override fun onClick(event: InventoryClickEvent, session: GUISession) {

        session.toNewPage(GUISession.IDENTIFIER_MAIN_PAGE)
        session.internalDataStream.remove(EnchantmentMerchant.GUI_TAG_SELECTED_LEVEL)
        session.internalDataStream.remove(EnchantmentMerchant.GUI_TAG_SELECTED_ENCHANTMENT)

    }


}