package me.aeolus.servertweaks.util.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.NullPointerException
import kotlin.reflect.KClass

open class GUISession(val player : Player, val pages : HashMap<String, GUIPage>) {

    companion object {

        const val IDENTIFIER_MAIN_PAGE = "__main__"

    }

    val internalDataStream = HashMap<String, Any>()
    var currentPage : GUIPage

    var switchingPage = false
        private set

    init {

        try {

            currentPage = pages[IDENTIFIER_MAIN_PAGE]!!

        } catch( e : Exception ) {

            throw IllegalStateException("Improper name formatting has been used for the gui.")
            e.printStackTrace()

        }

        player.openInventory(currentPage.render())

    }

    fun toNewPage(id : String) : Boolean {

        val newPage = pages[id]

        if(newPage != null) {

            switchingPage = true

            currentPage = newPage
            player.openInventory(newPage.render())

            switchingPage = false

            return true

        }

        return false

    }

    fun getPage(id : String) : GUIPage? = pages[id]

    fun updateCurrentPage() {

        switchingPage = true

        player.openInventory(currentPage.render())

        switchingPage = false
    }

    fun close() {

        player.closeInventory()

    }

    fun onCallClick(e : InventoryClickEvent) {

        currentPage.buttonInteract(e.slot, e, this)

    }

    fun onCallClose( e : InventoryCloseEvent) {

        //GUISessionManager.removeSession(this)

    }


}