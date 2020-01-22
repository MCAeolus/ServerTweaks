package me.aeolus.servertweaks.util


import net.minecraft.server.v1_14_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

object NBTItemTagUtil {

    fun addTag(key : String, value : String, item : ItemStack) : ItemStack {

        val nmsItemStack = CraftItemStack.asNMSCopy(item)

        val compound = nmsItemStack.tag ?: NBTTagCompound()
        compound.setString(key, value)

        nmsItemStack.tag = compound

        return CraftItemStack.asBukkitCopy(nmsItemStack)
    }

    fun getTag(key : String, item : ItemStack) : String? {

        val nmsItemStack = CraftItemStack.asNMSCopy(item)

        val compound = nmsItemStack.tag

        if(compound != null) {

            return compound.getString(key)

        }

        return null
    }

    fun hasTag(key : String, item : ItemStack) : Boolean {

        val nmsItemStack = CraftItemStack.asNMSCopy(item)

        val compound = nmsItemStack.tag

        if(compound != null) {

            return compound.hasKey(key)

        }

        return false
    }

    fun removeTag(key : String, item : ItemStack) : ItemStack {

        val nmsItemStack = CraftItemStack.asNMSCopy(item)

        val compound = nmsItemStack.tag
        compound?.remove(key)

        nmsItemStack.tag = compound

        return CraftItemStack.asBukkitCopy(nmsItemStack)
    }

    fun addTags(tags : HashMap<String, String>, item : ItemStack) : ItemStack {

        val nmsItemStack = CraftItemStack.asNMSCopy(item)

        val compound = nmsItemStack.tag ?: NBTTagCompound()
        tags.forEach { compound.setString(it.key, it.value) }

        nmsItemStack.tag = compound

        return CraftItemStack.asBukkitCopy(nmsItemStack)

    }

    fun getAllTags(item : ItemStack) : List<Pair<String, String>> {

        val retList = ArrayList<Pair<String, String>>()

        val nmsItemStack = CraftItemStack.asNMSCopy(item)

        val compound = nmsItemStack.tag

        if(compound != null) {

            for(k in compound.keys)
                retList.add(Pair(k, compound.get(k).toString()))

        }

        return retList
    }



}