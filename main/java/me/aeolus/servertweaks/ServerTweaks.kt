package me.aeolus.servertweaks

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import me.aeolus.servertweaks.modules.Manager
import me.aeolus.servertweaks.modules.headbounty.HeadBounty
import me.aeolus.servertweaks.modules.spawnworld.SpawnWorld
import me.aeolus.servertweaks.util.gui.GUISessionManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.io.InvalidClassException
import java.util.logging.Level.INFO

class ServerTweaks : JavaPlugin() {


    companion object {
        var plugin : ServerTweaks? = null
            private set

        var config : FileConfiguration? = null
            private set

        var permissionAPI : Permission? = null
            private set

        var chatAPI : Chat? = null
            private set

        var economyAPI : Economy? = null
            private set

    }

    override fun onLoad() {
        plugin = this

        ServerTweaks.config = this.config

        Manager()

        Manager.manager!!.onload()
    }


    override fun onEnable() {

        logger.log(INFO, "Attempting to hook into Vault API.")
        try {
            permissionAPI = getAPI<Permission>().provider
            chatAPI = getAPI<Chat>().provider
            economyAPI = getAPI<Economy>().provider
        } catch (e : Exception) {
            logger.warning("Could not load Vault API. Disabling plugin.")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
            return
        }

        logger.log(INFO, "Hooked into Vault successfully. Now loading plugin modules.")
        Manager.manager!!.create()
        GUISessionManager.init()

        logger.log(INFO, "Server Tweaks has finished loading.")
    }


    override fun onDisable() {

        logger.log(INFO, "Disabling modules.")
        GUISessionManager.exit()
        Manager.manager!!.close()


        logger.log(INFO, "Modules closed. Nulling Vault API.")
        permissionAPI = null
        chatAPI = null
        economyAPI = null

        plugin = null
        logger.log(INFO, "Server Tweaks has been disabled.")
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? { //Maybe re-write this to make it easier to interact with modules
        return SpawnWorld.CHUNK_GENERATOR
    }

    fun registerAsListener(l : Listener) {
        server.pluginManager.registerEvents(l, this)
    }

    fun unregisterListener(l : Listener) {
        HandlerList.unregisterAll(l)
    }

    fun pluginManager() = server.pluginManager

    private inline fun <reified T> getAPI() : RegisteredServiceProvider<T> {
        val provider = server.servicesManager.getRegistration(T::class.java)
        if(provider != null) return provider
        throw InvalidClassException("Class type has no provided API")
    }
}