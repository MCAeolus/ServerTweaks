package me.aeolus.servertweaks.modules.disablenaturalspawns

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.logging.Level

class DisableNaturalSpawns : Module, Listener {

    companion object {

        val disabledSpawnReasons = ArrayList<CreatureSpawnEvent.SpawnReason>()

        val removeList = ArrayList<Block>()

        init {

            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.NATURAL)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.REINFORCEMENTS)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.BREEDING)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.DISPENSE_EGG)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.EGG)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.ENDER_PEARL)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.NETHER_PORTAL)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.OCELOT_BABY)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.TRAP)
            disabledSpawnReasons.add(CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE)

        }

    }


    override fun create() {

        ServerTweaks.plugin!!.registerAsListener(this)


        Bukkit.getScheduler().scheduleSyncRepeatingTask(ServerTweaks.plugin!!, {

            if(removeList.isNotEmpty()) {

                ServerTweaks.plugin!!.logger.log(
                    Level.INFO,
                    "Disable Natural Spawns is now running through all spawners that need to be deleted. (size = ${removeList.size}"
                )

                val it = removeList.iterator()

                while(it.hasNext()) {

                    val b = it.next()
                    b.type = Material.AIR
                    it.remove()

                }

                ServerTweaks.plugin!!.logger.log(Level.INFO, "All spawners successfully removed.")

            }


        }, 100 * 60, 100 * 60)

    }

    override fun close() {

        ServerTweaks.plugin!!.unregisterListener(this)

    }

    @EventHandler
    fun spawnEvent(event : CreatureSpawnEvent) {

        if(disabledSpawnReasons.contains(event.spawnReason)) event.isCancelled = true

    }

    @EventHandler
    fun chunkLoadEvent(event : ChunkLoadEvent) {

        if(event.isNewChunk) {

            event.chunk.entities.forEach {
                it.remove()
            }

            event.chunk.tileEntities.forEach {
                if(it.type == Material.SPAWNER) removeList.add(it.block)
            }

        }

    }

}