package me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons

import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant.Companion.GUI_TAG_SELECTED_ITEM
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant.Companion.GUI_TAG_SELECTOR_PAGE
import me.aeolus.servertweaks.util.EnchantmentLib
import me.aeolus.servertweaks.util.NBTItemTagUtil
import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUISession
import me.aeolus.servertweaks.util.gui.genericButtons.StationaryButton
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ButtonSelectEnchantment(icon : ItemStack, val enchant : Enchantment) : GUIButton(icon, true) {

    override fun onClick(event: InventoryClickEvent, session: GUISession) {

        val subpage = session.getPage(GUI_TAG_SELECTOR_PAGE)!!
        subpage.removeButtons()
        subpage.name = "${ChatColor.GREEN}${ChatColor.BOLD}Select Level for ${EnchantmentLib.prettyEnchantName(enchant)}"

        val selectedItem = session.internalDataStream[GUI_TAG_SELECTED_ITEM]!! as ItemStack

        val startingLevel = selectedItem.getEnchantmentLevel(enchant) + 1

        if (startingLevel > 1) {

            for (i in 1 until startingLevel) {

                val completedLevelBook = ItemStack(Material.WRITTEN_BOOK)
                val completedLevelMeta = completedLevelBook.itemMeta

                completedLevelMeta.setDisplayName("${ChatColor.YELLOW}${ChatColor.BOLD}Your item already has this level!")
                completedLevelMeta.addEnchant(enchant, i, true)
                completedLevelBook.itemMeta = completedLevelMeta

                val button = StationaryButton(completedLevelBook)

                subpage.putButton(button = button)

            }

        }

        for (i in startingLevel..enchant.maxLevel) {

            val button = ButtonLevelSelector(
                i,
                enchant,
                EnchantmentMerchant.enchantmentCosts[enchant]!![i]!! - (if(startingLevel > 1) EnchantmentMerchant.enchantmentCosts[enchant]!![startingLevel - 1]!! else 0)
            )

            subpage.putButton(button = button)

        }

        val itemNamed = selectedItem.clone()
        val itemMeta = itemNamed.itemMeta
        itemMeta.setDisplayName("${ChatColor.YELLOW}${ChatColor.BOLD}Selected Item")
        itemNamed.itemMeta = itemMeta

        subpage.putButton(12, ButtonReturn())
        subpage.putButton(13, StationaryButton(itemNamed))
        subpage.putButton(14, ButtonAccept())

        session.internalDataStream[EnchantmentMerchant.GUI_TAG_SELECTED_ENCHANTMENT] = enchant

        session.toNewPage(GUI_TAG_SELECTOR_PAGE)
        session.player.playSound(session.internalDataStream[EnchantmentMerchant.MERCHANT_LOCATION_TAG]!! as Location, Sound.ENTITY_VILLAGER_TRADE, 0.7F, 1.0F)

    }
}