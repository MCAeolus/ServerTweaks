package me.aeolus.servertweaks.modules.spawnworld

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import me.aeolus.servertweaks.modules.spawnertweaks.SpawnerTweaks
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import java.util.*
import java.util.logging.Level

class SpawnWorld : Module, CommandExecutor {



    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if(s is Player) {

            if(args.isNotEmpty()) {

                val loc = Location(ServerTweaks.plugin!!.server.getWorld(args[0]), args[1].toDoubleOrNull() ?: 0.0, args[2].toDoubleOrNull() ?: 0.0, args[3].toDoubleOrNull() ?: 0.0)

                s.teleport(loc)

            }

        }

        return true
    }


    companion object {

        const val WORLD_NAME = "spawn_world"

        val CHUNK_GENERATOR = object : ChunkGenerator() {

            override fun generateChunkData(
                world: World,
                random: Random,
                x: Int,
                z: Int,
                biome: BiomeGrid
            ): ChunkData {

                return createChunkData(world)

            }

        }

    }

    override fun create() {

        val world = ServerTweaks.plugin!!.server.getWorld(WORLD_NAME)

        if(world == null) {

            val worldCreator = WorldCreator(WORLD_NAME)

            ServerTweaks.plugin!!.logger.log(Level.INFO, "Spawn world is being generated.")
            ServerTweaks.plugin!!.server.createWorld(worldCreator)

        }

        ServerTweaks.plugin!!.getCommand("worldtp")!!.setExecutor(this)

    }

    override fun close() {

        ServerTweaks.plugin!!.getCommand("worldtp")!!.setExecutor(null)


    }
}