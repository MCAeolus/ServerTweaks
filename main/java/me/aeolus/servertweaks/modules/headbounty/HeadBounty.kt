package me.aeolus.servertweaks.modules.headbounty

import me.aeolus.servertweaks.modules.Module
import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.util.MobHeads
import me.aeolus.servertweaks.util.NBTItemTagUtil
import me.aeolus.servertweaks.util.StringUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class HeadBounty : Listener, Module {

    companion object {
        var PERCENTAGE_LOST_ON_DEATH = 0.1
            private set

        var MOB_SKULL_COSTS = HashMap<EntityType, Int>()
            private set

        fun makeHeadBounty(p : OfflinePlayer, bounty : Int) : ItemStack {
            var headItem = ItemStack(Material.PLAYER_HEAD)
            val skullMet = headItem.itemMeta as SkullMeta

            skullMet.owningPlayer = p

            val lore = ArrayList<String>()
            lore.add("${ChatColor.GREEN}${ChatColor.BOLD}Worth at time of death: ${ChatColor.GOLD}$$bounty")
            lore.add("${ChatColor.YELLOW}At the time of selling, you will gain 10% of the victim's current balance.")

            skullMet.lore = lore
            skullMet.setDisplayName("${ChatColor.RED}${p.name}${ChatColor.RED}'s Head")
            headItem.itemMeta = skullMet

            val map = HashMap<String, String>()
            map["UUID"] = p.uniqueId.toString()
            map["isHeadBounty"] = "1"

            headItem = NBTItemTagUtil.addTags(map, headItem)

            return headItem
        }

        fun calculateBounty(p : OfflinePlayer) = (ServerTweaks.economyAPI!!.getBalance(p) * PERCENTAGE_LOST_ON_DEATH).roundToInt()

        fun isHeadBounty(item : ItemStack) = NBTItemTagUtil.hasTag("isHeadBounty", item)

        fun isMobBounty(item : ItemStack) = NBTItemTagUtil.hasTag("MOB", item)

        fun getMobBounty(item : ItemStack) : Int? {

            if(isMobBounty(item))
                return MOB_SKULL_COSTS[EntityType.valueOf(NBTItemTagUtil.getTag("MOB", item)!!)]

            return null
        }

        fun getBountyOwner(item : ItemStack) : OfflinePlayer? {

            if(isHeadBounty(item))
                return ServerTweaks.plugin!!.server.getOfflinePlayer(
                    getBountyOwnerUUID(
                        item
                    )!!)

            return null
        }

        fun getBountyOwnerUUID(item : ItemStack) : UUID? {

            if(isHeadBounty(item))
                return UUID.fromString(NBTItemTagUtil.getTag("UUID", item))

            return null
        }

        fun getMobSkullBounty(ent : EntityType ) : ItemStack? {

            if(ent.isAlive) {

                val entBase = MobHeads.getCorrespondingMobHead(ent)
                val entMeta = entBase!!.itemMeta

                val lore = ArrayList<String>()
                lore.add("${ChatColor.WHITE}This head is worth ${ChatColor.GOLD}$${MOB_SKULL_COSTS[ent]}")
                lore.add("${ChatColor.GREEN}Right-click in your hand to sell.")

                entMeta.lore = lore
                entMeta.setDisplayName("${ChatColor.GREEN}${StringUtil.romanCapitilizationSentence(ent.name.replace("_", " "))} Head")

                entBase.itemMeta = entMeta

                val map = HashMap<String, String>()
                map["MOB"] = ent.name
                map["isHeadBounty"] = "1"

                return NBTItemTagUtil.addTags(map, entBase)

            }

            return null

        }

    }


    override fun create() {
        ServerTweaks.plugin!!.registerAsListener(this)
        //ServerTweaks.plugin!!.getCommand("headsell")!!.setExecutor(HeadSellCommand())
        ServerTweaks.plugin!!.getCommand("headget")!!.setExecutor(DebugGiveHeadCommand())

        val conf = ServerTweaks.config!!

        if(!conf.contains("headbounty")) {

            val entMap = HashMap<String, Int>()

            for(e in EntityType.values())
                if(e.isAlive) {
                    entMap[e.name] = 1
                    MOB_SKULL_COSTS[e] = 1
                }



            conf.set("headbounty.playerPercentageonCashIn", 0.1)

            conf.set("headbounty.mobHeadCosts", entMap)

            ServerTweaks.plugin!!.saveConfig()

        } else {

            PERCENTAGE_LOST_ON_DEATH = conf.getDouble("headbounty.playerPercentageonCashIn")

            try {

                for( entry in conf.getConfigurationSection("headbounty.mobHeadCosts")!!.getValues(false)) {

                    MOB_SKULL_COSTS[EntityType.valueOf(entry.key)] = entry.value as Int

                }

            } catch ( e : Exception) {
                e.printStackTrace()
            }

        }

    }

    override fun close() {
        ServerTweaks.plugin!!.unregisterListener(this)
        //ServerTweaks.plugin!!.getCommand("headsell")!!.setExecutor(null)
        ServerTweaks.plugin!!.getCommand("headget")!!.setExecutor(null)
    }


    @EventHandler
    fun playerPickupEvent(event : EntityPickupItemEvent) {

        if(event.entityType == EntityType.PLAYER) {

            if (isHeadBounty(event.item.itemStack) && !isMobBounty(event.item.itemStack)) {

                if (getBountyOwnerUUID(event.item.itemStack)!! == (event.entity as Player).uniqueId) {
                    event.isCancelled = true
                    event.item.remove()
                    event.entity.sendMessage("${ChatColor.GREEN}You successfully recovered your bounty!")
                }

            }
        }

    }

    @EventHandler
    fun playerRightClickEvent(event : PlayerInteractEvent) {

        val inHand = event.item

        if(inHand != null && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {

            if(isHeadBounty(inHand)) {

                event.isCancelled = true

                if(isMobBounty(inHand)) {

                    val bountyCost = getMobBounty(inHand)

                    if(bountyCost != null) {

                        val totalBountyCost = bountyCost * inHand.amount


                        ServerTweaks.economyAPI!!.depositPlayer(event.player, totalBountyCost.toDouble())

                        event.player.sendMessage("${ChatColor.GREEN}You sold ${ChatColor.YELLOW}${inHand.amount} ${ChatColor.WHITE}mob ${ChatColor.GREEN}head${if(inHand.amount > 1)"s" else ""} for ${ChatColor.GOLD}$$totalBountyCost${ChatColor.GREEN}.")

                        when (event.hand) {
                            EquipmentSlot.HAND -> event.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                            EquipmentSlot.OFF_HAND -> event.player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                            else -> return
                        }

                    }

                } else {

                    val bountyPlayer = getBountyOwner(inHand)

                    val bountyCost =
                        calculateBounty(bountyPlayer!!)

                    ServerTweaks.economyAPI!!.withdrawPlayer(bountyPlayer, bountyCost.toDouble())

                    if (bountyPlayer.isOnline) {

                        (bountyPlayer as Player).sendMessage("${ChatColor.RED}You lost your head (and ${ChatColor.GOLD}$$bountyCost${ChatColor.RED}) to ${ChatColor.GOLD}${event.player.displayName}${ChatColor.RED}.")

                    }

                    ServerTweaks.economyAPI!!.depositPlayer(event.player, bountyCost.toDouble())
                    event.player.sendMessage("${ChatColor.GREEN}You sold ${ChatColor.WHITE}${bountyPlayer.name}'s ${ChatColor.GREEN}head for ${ChatColor.GOLD}$$bountyCost${ChatColor.GREEN}.")

                    when (event.hand) {
                        EquipmentSlot.HAND -> event.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                        EquipmentSlot.OFF_HAND -> event.player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                        else -> return
                    }

                }
            }

        }


    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerDeathEvent(event : PlayerDeathEvent) {

        val bounty = calculateBounty(event.entity)
        event.entity.sendMessage("${ChatColor.RED}You lost your head when you died! It is currently worth ${ChatColor.GOLD}$$bounty${ChatColor.RED}!")

        val headItem =
            makeHeadBounty(event.entity, bounty)

        event.entity.world.dropItem(event.entity.eyeLocation, headItem)
    }

    /**fun playerDeathEvent(event : EntityDamageByEntityEvent) { //TODO only caused by other players?
        if(event.entity.) {
            ServerTweaks.plugin!!.logger.log(INFO, "dead")

            if (event.damager.type == EntityType.PLAYER && event.entityType == EntityType.PLAYER) { //for now assume killer should be player

                val victim = event.entity as Player

                val bounty = ServerTweaks.economyAPI!!.getBalance(victim) * PERCENTAGE_LOST_ON_DEATH
                ServerTweaks.economyAPI!!.withdrawPlayer(victim, bounty)
                victim.sendMessage("${ChatColor.RED}You lost ${ChatColor.GOLD}$$bounty ${ChatColor.RED}due to dying.")

                val headItem = ItemStack(Material.PLAYER_HEAD)
                val skullMet = headItem.itemMeta as SkullMeta

                skullMet.owningPlayer = victim

                val lore = ArrayList<String>()
                lore.add("${ChatColor.GREEN}${ChatColor.BOLD}Worth: ${ChatColor.GOLD}$$bounty")

                skullMet.lore = lore
                skullMet.setDisplayName("${ChatColor.RED}${victim.displayName}'s Head")
                headItem.itemMeta = skullMet

                victim.world.dropItem(victim.eyeLocation, headItem)
                victim.world.spawnParticle(Particle.REDSTONE, victim.eyeLocation, 3)
            }
        }


    }**/

}