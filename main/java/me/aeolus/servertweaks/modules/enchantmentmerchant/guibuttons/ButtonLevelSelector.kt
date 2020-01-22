package me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant
import me.aeolus.servertweaks.util.EnchantmentLib
import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUISession
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ButtonLevelSelector(val level : Int, val enchant : Enchantment, val cost : Int) : GUIButton(makeItem(false, level, enchant, cost), true) {

    var isChosen = false
        private set

    companion object {

        fun makeItem(isChosen : Boolean, level : Int, enchant : Enchantment, cost : Int) : ItemStack {

            if(isChosen) {

                val chosenIcon = ItemStack(Material.PAPER)
                val chosenMeta = chosenIcon.itemMeta

                chosenMeta.setDisplayName("${ChatColor.YELLOW}${ChatColor.BOLD}LEVEL $level SELECTED")
                chosenMeta.lore = listOf("${ChatColor.WHITE}Confirm your selection below, or choose another option.")
                chosenIcon.itemMeta = chosenMeta

                return chosenIcon

            } else {

                return EnchantmentLib.makeEnchantmentBook(
                    enchant,
                    level,
                    "${ChatColor.YELLOW}Level $level",
                    listOf("${ChatColor.WHITE}Select this book for a level $level enchantment.", "${ChatColor.WHITE}Costs: ${ChatColor.GREEN}$$cost")
                )

            }

        }

    }

    override fun onClick(event: InventoryClickEvent, session: GUISession) {

        if(!isChosen) {

            if(ServerTweaks.economyAPI!!.getBalance(session.player) < cost) {

                session.player.playSound(session.player.location, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
                session.player.sendMessage("${ChatColor.RED}You don't have the economy to buy that!")

                return
            }


            changeState(true)

            val currentSelected = session.internalDataStream[EnchantmentMerchant.GUI_TAG_SELECTED_LEVEL] as Int?

            if (currentSelected != null && currentSelected != level)
                (session.currentPage.getButton(currentSelected - 1) as ButtonLevelSelector?)?.changeState(false)

            val acceptButton = session.currentPage.getButton<ButtonAccept>()!!

            if (acceptButton.isLocked)
                acceptButton.changeState(false)

            session.updateCurrentPage()

            session.internalDataStream[EnchantmentMerchant.GUI_TAG_SELECTED_LEVEL] = level
            session.player.playSound(session.player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F)

        }

    }

    fun changeState(isChosen : Boolean) {

        this.isChosen = isChosen

        updateIcon(makeItem(isChosen, level, enchant, cost))

    }
}