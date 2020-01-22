package me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Manager
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant.Companion.GUI_TAG_SELECTED_ENCHANTMENT
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant.Companion.GUI_TAG_SELECTED_ITEM
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant.Companion.GUI_TAG_SELECTED_LEVEL
import me.aeolus.servertweaks.util.EnchantmentLib
import me.aeolus.servertweaks.util.StringUtil
import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUISession
import me.aeolus.servertweaks.util.gui.GUISessionManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class ButtonAccept() : GUIButton(acceptButtonLocked, true) {

    var isLocked = true
        private set

    companion object {

        private val acceptButtonLocked = ItemStack(Material.BEDROCK)
        private val acceptButtonUnlocked = ItemStack(Material.GREEN_STAINED_GLASS)

        init {
            val lockedAcceptMeta = acceptButtonLocked.itemMeta
            lockedAcceptMeta.setDisplayName("${ChatColor.GRAY}MAKE A SELECTION FIRST")
            lockedAcceptMeta.lore = listOf(
                "${ChatColor.WHITE}Select the level you wish to",
                "${ChatColor.WHITE}apply for your enchantment above!"
            )
            acceptButtonLocked.itemMeta = lockedAcceptMeta

            val unlockedAcceptMeta = acceptButtonUnlocked.itemMeta
            unlockedAcceptMeta.setDisplayName("${ChatColor.GREEN}${ChatColor.BOLD}Confirm Selection")
            acceptButtonUnlocked.itemMeta = unlockedAcceptMeta
        }
    }

    fun changeState(isLocked : Boolean, session : GUISession? = null) {
        this.isLocked = isLocked

        if(isLocked) updateIcon(acceptButtonLocked)
        else updateIcon(acceptButtonUnlocked)

        session?.updateCurrentPage()

    }

    override fun onClick(event: InventoryClickEvent, session: GUISession) {

        if(!isLocked) {

            val enchant = session.internalDataStream[GUI_TAG_SELECTED_ENCHANTMENT]!! as Enchantment
            val level = session.internalDataStream[GUI_TAG_SELECTED_LEVEL]!! as Int

            val cost = EnchantmentMerchant.enchantmentCosts[enchant]!![level]!!

            ServerTweaks.economyAPI!!.withdrawPlayer(session.player, cost.toDouble())
            session.player.sendMessage("${ChatColor.GREEN}You successfully enchanted your held item with ${EnchantmentLib.prettyEnchantName(enchant)} ${StringUtil.intToRomanNumerals(level)}!")

            val item = session.internalDataStream[GUI_TAG_SELECTED_ITEM]!! as ItemStack
            item.addEnchantment(enchant, level)

            session.player.inventory.setItem(EquipmentSlot.HAND, item)

            GUISessionManager.removeSession(session)

            Manager.manager!!.get<EnchantmentMerchant>()!!.completedAnimation(session.player)

            //session.player.playSound(session.player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)

        }

    }
}