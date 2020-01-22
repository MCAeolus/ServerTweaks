package me.aeolus.servertweaks.spawnboosters

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class SpawnBoosters : Module, Listener, CommandExecutor {


    val boosterMap = ArrayList<Pair<Block, Block>>()

    private val playerSelectionSessions = HashMap<Player, SpawnBoosterSelectionSession>()

    private data class SpawnBoosterSelectionSession(val booster1 : Block?, val booster2 : Block?)

    override fun create() {

        ServerTweaks.plugin!!.registerAsListener(this)
        ServerTweaks.plugin!!.getCommand("spawnboosters")!!.setExecutor(this)

        val conf = ServerTweaks.config!!

        if(conf.contains("spawnboosters")) {

         //TODO load in data

        }

    }

    override fun close() {

        for (pair in boosterMap) {

            //TODO serialize

        }

        ServerTweaks.plugin!!.saveConfig()

        ServerTweaks.plugin!!.getCommand("spawnboosters")!!.setExecutor(null)
        ServerTweaks.plugin!!.unregisterListener(this)

    }

    fun lerp(progress : Double, xi : Double, xf : Double) : Double = ((1 - progress) * xi) + (progress * xf)

    //fun gravPull(vo : Double, )

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if(args.isNotEmpty() && s is Player) {

            val session = playerSelectionSessions[s] ?: SpawnBoosterSelectionSession(null, null)

            if(session.booster1 == null) {



            } else if(session.booster2 == null) {



            }


        }

        return true
    }


}