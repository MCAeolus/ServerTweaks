package me.aeolus.servertweaks.modules

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.crates.Crates
import me.aeolus.servertweaks.modules.disableitems.DisableItems
import me.aeolus.servertweaks.modules.disablenaturalspawns.DisableNaturalSpawns
import me.aeolus.servertweaks.modules.enchantmentmerchant.EnchantmentMerchant
import me.aeolus.servertweaks.modules.extracommands.ExtraCommands
import me.aeolus.servertweaks.modules.headbounty.HeadBounty
import me.aeolus.servertweaks.modules.spawnertweaks.SpawnerTweaks
import me.aeolus.servertweaks.modules.spawnworld.SpawnWorld
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

class Manager : CommandExecutor {

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean { // admin command


        //TODO TODO TODO MUST ADD IN PROPER CREATION/CLOSING STATES FOR EACH MODULE (and checks)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if(args.isNotEmpty()) {

            when(args[0].toLowerCase()) {
                "c",
                "close" -> {
                    if(args.size > 1) {
                        var module : Module? = null
                        val sanitizedInput = args[1].toLowerCase()

                        for(m in ModuleID.values())
                            if(m.name.toLowerCase() == sanitizedInput || m.id.toLowerCase() == sanitizedInput) {
                                module = get(m)
                                break
                            }

                        if(module != null) {
                            module.close()
                            s.sendMessage("Module successfully shut down.")
                        } else {
                            s.sendMessage("Could not find module '${args[1]}'. Listed below are valid modules.")
                            ModuleID.values().forEach { s.sendMessage("${it.name} (${it.id}") }
                        }

                    } else {
                        s.sendMessage("Wrong formatting. /$lbl ${args[0]} ${ChatColor.RED}<(MISSING: string) module>")
                    }
                }
                "o",
                "open" -> {
                    if(args.size > 1) {
                        var module : Module? = null
                        val sanitizedInput = args[1].toLowerCase()

                        for(m in ModuleID.values())
                            if(m.name.toLowerCase() == sanitizedInput || m.id.toLowerCase() == sanitizedInput) {
                                module = get(m)
                                break
                            }

                        if(module != null) {
                            module.create()
                            s.sendMessage("Module successfully opened.")
                        } else {
                            s.sendMessage("Could not find module '${args[1]}'. Listed below are valid modules.")
                            ModuleID.values().forEach { s.sendMessage("${it.name} (${it.id}") }
                        }

                    } else {
                        s.sendMessage("Wrong formatting. /$lbl ${args[0]} ${ChatColor.RED}<(MISSING: string) module>")
                    }
                }
            }

        }

        return true
    }

    enum class ModuleID(val id : String) {
        HEAD_BOUNTY(HeadBounty::class.simpleName!!),
        CRATES(Crates::class.simpleName!!),
        SPAWNER_TWEAKS(SpawnerTweaks::class.simpleName!!),
        //COMMAND_INTERCEPTOR(CommandInterceptor::class.simpleName!!),
        DISABLE_NATURAL_SPAWNS(DisableNaturalSpawns::class.simpleName!!),
        SPAWN_WORLD(SpawnWorld::class.simpleName!!),
        ENCHANTMENT_MERCHANT(EnchantmentMerchant::class.simpleName!!),
        DISABLE_ITEMS(DisableItems::class.simpleName!!),
        EXTRA_COMMANDS(ExtraCommands::class.simpleName!!),

    }

    companion object {

        var manager : Manager? = null
            private set
    }

    private var isCreated = false
    private var moduleMap = HashMap<String, Module>()

    private val loadList = ArrayList<Module>()

    init {

        if(manager == null) {
            manager = this
        }

        registerModule<HeadBounty>()
        registerModule<Crates>()
        registerModule<SpawnerTweaks>()
        //registerModule<CommandInterceptor>()
        registerModule<DisableNaturalSpawns>()
        registerModule<SpawnWorld>()
        registerModule<EnchantmentMerchant>()
        registerModule<DisableItems>()
        registerModule<ExtraCommands>()

        for( entry in moduleMap ) {

            val depends = entry.value::class.findAnnotation<Depend>()

            if(depends != null) {

                //TODO write this out for ordering the dependencies

            }

        }

    }

    private inline fun <reified T : Module> registerModule() {
        try {
            val name = T::class.simpleName
            moduleMap[name!!] = T::class.primaryConstructor!!.call()
        } catch ( e : Exception ) {
            ServerTweaks.plugin!!.logger.warning("Could not load module! ${T::class.jvmName}")
            e.printStackTrace()
        }
    }

    fun onload() {

        moduleMap.values.forEach { it.onload() }

    }

    fun create() {
        if(!isCreated) {

            ServerTweaks.plugin!!.getCommand("tweakmanager")!!.setExecutor(this) //bold call

            moduleMap.values.forEach { createModule(it) }

            isCreated = true
        }
    }

    fun get(mod : String) : Module? = moduleMap[mod]

    fun get(mod : ModuleID) : Module = moduleMap[mod.id]!!

    inline fun <reified T : Module> get() : T? {

        for( m in modules() ) if(m is T) return m

        return null

    }

    fun modules() = moduleMap.values

    fun createModule(mod : Module) {
        mod.create()
    }

    fun createModule(modName : String) {
        if(moduleMap.containsKey(modName))
            createModule(modName)
    }

    fun closeModule(mod : Module) {
        mod.close()
    }

    fun closeModule(modName : String) {
        if(moduleMap.containsKey(modName))
            closeModule(moduleMap[modName]!!)
    }

    fun close() {

        moduleMap.values.forEach { closeModule(it) }

        manager = null
    }

}