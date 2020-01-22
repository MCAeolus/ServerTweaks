package me.aeolus.servertweaks.util.gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUIPage(rows : Int, val defaultItem : ItemStack = ItemStack(Material.AIR), var name : String) {

    companion object {
        const val COLUMNS = 9
    }

    val size = rows * COLUMNS

    init {

    }

    private val inventoryButtons = Array<GUIButton?>(size) { null }

    fun render() : Inventory {

        val inv = Bukkit.createInventory(null, size, name)

        for(i in 0 until size) inv.setItem(i, inventoryButtons[i]?.getIcon())

        return inv

    }

    fun getButton(location : Int) : GUIButton? = try { inventoryButtons[location] } catch ( e : Exception ) { null }

    fun getButtons() : Array<GUIButton?> = inventoryButtons

    fun removeButtons() {

        for( i in 0 until size ) inventoryButtons[i] = null

    }

    inline fun <reified T : GUIButton> getButton() : T? {

        for(i in getButtons()) if(i is T) return i

        return null

    }

    fun buttonInteract(location : Int, event : InventoryClickEvent, session : GUISession) {
        val button = getButton(location)

        if(button != null) {
            event.isCancelled = button.shouldCancel
            button.onClick(event, session)
        }

    }

    fun putButton(location : Int = -1, button : GUIButton) : GUIPage {

        if(location >= size) throw IllegalArgumentException("Location passed is not valid for the desired page.")

        if(location < 0) {

            var firstNull = -1
            for( i in 0 until size ) if(inventoryButtons[i] == null) {
                firstNull = i
                break
            }

            if(firstNull > -1) {

                inventoryButtons[firstNull] = button

            }

        } else {

            inventoryButtons[location] = button

        }

        return this

    }

    fun removeButton(location : Int) : GUIPage {

        if(location > -1 && location < size) inventoryButtons[location] = null

        return this

    }

}
