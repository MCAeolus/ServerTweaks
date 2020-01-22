package me.aeolus.servertweaks.modules.crates

import com.sk89q.worldedit.event.platform.BlockInteractEvent
import com.sk89q.worldedit.event.platform.Interaction
import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Depend
import me.aeolus.servertweaks.modules.Module
import me.aeolus.servertweaks.modules.spawnertweaks.SpawnerTweaks
import me.aeolus.servertweaks.modules.spawnworld.SpawnWorld
import me.aeolus.servertweaks.util.AnimatedParticleSession
import me.aeolus.servertweaks.util.NBTItemTagUtil
import net.minecraft.server.v1_14_R1.NBTTagByte
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.collections.forEach as forEach1


@Depend<SpawnWorld>
class Crates : Module, Listener, CommandExecutor {

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if(s is Player) {

            if(args.isNotEmpty()) {

                val arg1 = args[0].toLowerCase()
                var cset : CrateSet? = null

                for( cs in availableChests ) if(cs.name == arg1) {
                    cset = cs
                    break
                }

                if(cset != null) {

                    if(args.size > 1) {

                        val arg2 = args[1].toLowerCase()

                        when(arg2) {

                            "givekey", "gk" -> {

                                s.inventory.addItem(cset.key)
                                s.sendMessage("You were given a ${cset.displayName} crate key.")

                            }
                            "setholder", "sh" -> {

                                val targetedChest = s.getTargetBlock(15)

                                if(targetedChest != null && targetedChest.state is Chest) {

                                    chests[cset]?.holo?.remove()

                                    chests[cset] = SessionCrateData(targetedChest.state as Chest, makeHolo(cset, targetedChest.state as Chest))
                                    s.sendMessage("Targeted block is now a ${cset.displayName} crate.")

                                } else
                                    s.sendMessage("${ChatColor.RED}Invalid targeted block.")

                            }

                        }

                    } else
                        s.sendMessage("${ChatColor.RED}Available sub-commands: givekey [gk], setholder [sh]")

                } else {
                    s.sendMessage("${ChatColor.RED}Unknown crate type. Available crates: ")
                    for( cs in availableChests ) s.sendMessage(cs.name)
                    s.sendMessage("${ChatColor.YELLOW}Command layout: /$lbl <crate> <givekey/setholder>")
                }


            }

        }


        return true
    }

    companion object {


         val availableChests = listOf(

         CrateSet("test","Test",
            WeightedTable(listOf(
                WeightedResult(0.1, ItemStack(Material.DIRT), "test1"),
                WeightedResult(0.6, ItemStack(Material.SPONGE), "test2"),
                WeightedResult(0.3, ItemStack(Material.GRINDSTONE), "test3")
            ))
         )

         )

        fun createKey(base : Material, displayName : String, name : String) : ItemStack {

            val stack = ItemStack(base)
            val stackMeta = stack.itemMeta

            stackMeta.setDisplayName(displayName + "${ChatColor.RESET}${ChatColor.WHITE}${ChatColor.BOLD} KEY")
            stackMeta.lore = listOf(
                "${ChatColor.WHITE}Use this key on the appropriate '$displayName${ChatColor.RESET}${ChatColor.WHITE}' crate",
                "at spawn and find out what you won!"
            ) //TODO maybe a command to preview all possible crate items

            stack.itemMeta = stackMeta

            return NBTItemTagUtil.addTag("crate", name, stack)

        }

    }

    data class SessionCrateData(val chest : Chest, val holo : ArmorStand)

    data class CrateSet(val name : String, val displayName : String, val table : WeightedTable) {

        val key : ItemStack = createKey(Material.TRIPWIRE_HOOK, displayName, name)

    }

    data class WeightedResult(val weight : Double, val item : ItemStack?, val data : String?)

    data class WeightedTable(val weights : List<WeightedResult>) {

        val total : Double = weights.sumByDouble { it.weight }

        fun random() : WeightedResult {

            val partition = total * Math.random() //[0.0, 1.0)
            var currentPos = 0.0
            for( w in weights ) {

                currentPos += w.weight
                if(currentPos >= partition) return w

            }

            return weights.first()
        }

    }

    val chests = HashMap<CrateSet, SessionCrateData>()

    override fun create() {

        ServerTweaks.plugin!!.getCommand("crateadmin")!!.setExecutor(this)

        if(ServerTweaks.config!!.contains("crates")) {

            for( cs in availableChests ) {

                val dat = ServerTweaks.config!!.getConfigurationSection("crates.${cs.name}")

                if(dat != null) {

                    val serialiedLocation = HashMap<String, Any>()

                    for( ent in dat.getValues(false) )
                        serialiedLocation[ent.key] = ent.value

                    val loc = Location.deserialize(serialiedLocation)

                    val blockHolder = loc.block

                    if(blockHolder is Chest) {


                        chests[cs] = SessionCrateData(blockHolder, makeHolo(cs, blockHolder))


                    }

                }

            }

        } else ServerTweaks.plugin!!.logger.warning("No crates have been setup on the server.")

    }

    fun makeHolo(cs : CrateSet, bh : Chest) : ArmorStand {

        val holo = bh.world.spawn(bh.location.add(0.5, 1.0, 0.5), ArmorStand::class.java)

        holo.isVisible = false

        holo.isCustomNameVisible = true
        holo.customName = cs.displayName

        holo.isInvulnerable = true
        holo.isSmall = true
        holo.isMarker = true
        holo.setGravity(false)


        return holo

    }

    override fun close() {

        ServerTweaks.plugin!!.getCommand("crateadmin")!!.setExecutor(null)

        for( set in chests ) {
            ServerTweaks.config!!.set("crates.${set.key.name}", set.value.chest.location.serialize())
            set.value.holo.remove()
        }

        ServerTweaks.plugin!!.saveConfig()

    }

    @EventHandler
    fun interact( event : PlayerInteractEvent ) {

        if(event.action == Action.RIGHT_CLICK_BLOCK ) {

            val inHand = event.item

            if(inHand != null) {

                val tagData = NBTItemTagUtil.getTag("crate", inHand)

                if(tagData != null) {
                    for (cs in availableChests) {

                        if (cs.name == tagData) {

                            if(chests[cs]!!.chest == event.clickedBlock!!.state) {
                                event.isCancelled = true

                                doOpening(cs, chests[cs]!!, event.player)
                                inHand.amount = inHand.amount - 1

                            }
                            return

                        }


                    }
                }

            }
        }

    }

    fun doOpening(cs : CrateSet, session : SessionCrateData, p : Player) {

        p.inventory.addItem(cs.table.random().item)

    }

}