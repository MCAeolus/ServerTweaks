package me.aeolus.servertweaks.util.gui

import me.aeolus.servertweaks.ServerTweaks
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import kotlin.reflect.KClass

object GUISessionManager : Listener {

    val sessionMap = HashMap<Player, GUISessionDescriptor>()

    fun init() {

        ServerTweaks.plugin!!.registerAsListener(this)

    }

    fun exit() {

        ServerTweaks.plugin!!.unregisterListener(this)

        for( e in sessionMap.keys ) e.closeInventory()

    }

    inline fun <reified T> getSession(p : Player) : GUISession? {

        val ownerReference = T::class

        val ref = sessionMap[p]

        return if(ref != null && ref.ownerReference == ownerReference) ref.session
               else null
    }

    inline fun <reified T> addSession(p : Player, session : GUISession) : Boolean {

        if(!sessionMap.containsKey(p)) {
            sessionMap[p] = GUISessionDescriptor(T::class, session)
            return true
        }

        return false

    }

    inline fun <reified T> removeSession(p : Player) : Boolean {

        val ref = sessionMap[p]

        if(ref != null && ref.ownerReference == T::class) {

            ref.session.close()
            sessionMap.remove(p)
            return true

        }

        return false

    }

    fun removeSession(session : GUISession) : Boolean {

        for(e in sessionMap) {

            if(e.value.session == session) {

                session.player.closeInventory()

                sessionMap.remove(e.key)
                return true

            }

        }

        return false

    }

    @EventHandler
    fun invClick(event : InventoryClickEvent) {

        val ref = sessionMap[event.whoClicked as Player]

        if(ref != null) {

            if(event.rawSlot <= ref.session.currentPage.size) ref.session.onCallClick(event)
            else event.isCancelled = true

        }

    }

    @EventHandler
    fun invDrag(event : InventoryDragEvent) {

        if(sessionMap.containsKey(event.whoClicked)) event.isCancelled = true

    }

    @EventHandler
    fun invClose(event : InventoryCloseEvent) {

        val ref = sessionMap[event.player as Player]

        if(ref != null && !ref.session.switchingPage) {

            ref.session.onCallClose(event)

            sessionMap.remove(event.player as Player)

        }

    }

    data class GUISessionDescriptor(val ownerReference : KClass<*>, val session : GUISession)
}