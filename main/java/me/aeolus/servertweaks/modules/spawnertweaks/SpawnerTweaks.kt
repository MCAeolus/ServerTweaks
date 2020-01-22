package me.aeolus.servertweaks.modules.spawnertweaks

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import me.aeolus.servertweaks.modules.headbounty.HeadBounty
import me.aeolus.servertweaks.util.MobHeads
import me.aeolus.servertweaks.util.NBTItemTagUtil
import me.aeolus.servertweaks.util.StringUtil
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import java.util.function.Predicate
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class SpawnerTweaks : Module, Listener {

    companion object {

        const val STACKED_ARMOR_STAND_SCOREBOARD_TAG = "ServerTweaks_STACKED_SPAWNER_STAND"
        const val SPAWNER_MAX_STACK_SIZE = 32
        const val MARKED_ENTITY_METADATA = "ServerTweaks_SPAWNER_ENT"
        const val ENTITY_NBT_KEY = "entity"
        const val STACKED_NBT_KEY = "stacked"
        const val SCOREBOARD_TAG_STACKS = "SERVERTWEAKS_standStack"
        const val MAX_MOB_STACK_LEVEL = 1000

        fun setSpawnerEntityType(block : Block, entity : EntityType) {

            if(block.type != Material.SPAWNER) return

            val spawnerState = block.state as CreatureSpawner

            spawnerState.spawnedType = entity

            spawnerState.update()

        }

        private fun spawnerBaseItem(type : EntityType) : ItemStack {

            val itemBase = ItemStack(Material.SPAWNER)

            val itemMeta = itemBase.itemMeta
            itemMeta!!.setDisplayName("${ChatColor.RESET}${StringUtil.romanCapitilizationSentence(type.name.replace('_', ' '))} Spawner")
            itemBase.itemMeta = itemMeta

            return NBTItemTagUtil.addTag(ENTITY_NBT_KEY, type.name, itemBase)

        }

        fun createStackedSpawnerItem(type : EntityType, stacks : Int) : ItemStack {

            val baseSpawner = spawnerBaseItem(type)

            val useStacks = when {
                stacks > SPAWNER_MAX_STACK_SIZE -> SPAWNER_MAX_STACK_SIZE
                stacks < 1 -> 1
                else -> stacks
            }


            val itemMeta = baseSpawner.itemMeta
            itemMeta!!.setDisplayName("${itemMeta.displayName} ${ChatColor.GOLD}${ChatColor.BOLD}x $useStacks")
            baseSpawner.itemMeta = itemMeta

            return NBTItemTagUtil.addTag(STACKED_NBT_KEY, useStacks.toString(), baseSpawner)

        }

        fun getEntityTypeForSpanwerItem(item : ItemStack) : EntityType {

            val tag = NBTItemTagUtil.getTag(ENTITY_NBT_KEY, item)

            return if (tag != null) EntityType.valueOf(tag)
                   else EntityType.UNKNOWN

        }

        fun getSpawnerStackSize(block : Block) : Int {

            val ent = getStackerEntity(block)

            if(ent != null) {

                val tag = getScoreboardTag(SCOREBOARD_TAG_STACKS, ent)

                if(tag != null) return tagToSize(SCOREBOARD_TAG_STACKS, tag)

            }

            return 1

        }

        fun getScoreboardTag(tagBase : String, entity : Entity) = entity.scoreboardTags.firstOrNull { it.startsWith(SCOREBOARD_TAG_STACKS) }

        fun addSpawnerStacks(block : Block, count : Int, unitSize : Int = 1) : Pair<Boolean, Int> {

            var ent = getStackerEntity(block)

            if ( ent == null && block.type == Material.SPAWNER ) {

                ent = createStackingEntity(block)

            }

            if(ent != null) {

                val tag = getScoreboardTag(SCOREBOARD_TAG_STACKS, ent)

                if (tag != null) {

                    val currentSize = tagToSize(SCOREBOARD_TAG_STACKS, tag)

                    if (currentSize < SPAWNER_MAX_STACK_SIZE) {

                        var current = count
                        var newSize = currentSize


                        for(i in 1..count)
                            if(newSize + unitSize <= SPAWNER_MAX_STACK_SIZE) {
                                newSize += unitSize
                                current--
                            }


                        ent.removeScoreboardTag(tag)
                        ent.addScoreboardTag(sizeToTag(newSize))
                        ent.customName = formatStackedSpawnerName(
                            (block.state as CreatureSpawner).spawnedType,
                            newSize
                        )

                        return Pair(current == 0, count - current)

                    } else return Pair(false, 0)

                }
            }

            return Pair(false, 0)

        }

        fun getStackerEntity(block : Block) : ArmorStand? {

            if(block.type == Material.SPAWNER) {

                val pred : (Entity) -> (Boolean) = { it.type == EntityType.ARMOR_STAND }
                val stands = block.world.getNearbyEntities(block.location.add(0.5, 1.0, 0.5), 0.5, 1.0, 0.5, pred)

                if(stands.isNotEmpty()) {

                    if(getScoreboardTag(STACKED_ARMOR_STAND_SCOREBOARD_TAG, stands.first()) != null) return stands.first() as ArmorStand

                }

            }

            return null

        }

        fun getStackingMob(block : Block) : LivingEntity? {

            if(block.type == Material.SPAWNER) {

                val entType = (block.state as CreatureSpawner).spawnedType

                if(entType.isAlive) {

                    val pred: (Entity) -> (Boolean) = { it.type == entType }
                    val ents = block.world.getNearbyEntities(block.location.add(0.5, 1.0, 0.5), 0.5, 1.0, 0.5, pred)

                    if (ents.isNotEmpty()) {

                        if (ents.first().hasMetadata(MARKED_ENTITY_METADATA)) return ents.first() as LivingEntity

                    }

                }

            }

            return null

        }

        fun makeStackingMob(block : Block, lvl : Int) : LivingEntity? {

            if(block.type == Material.SPAWNER && (block.state as CreatureSpawner).spawnedType.isAlive) {

                val mob = block.world.spawnEntity(block.location.add(0.5, 1.0, 0.5), (block.state as CreatureSpawner).spawnedType) as LivingEntity

                mob.setAI(false)
                mob.isCustomNameVisible = true
                mob.customName = formatStackedMobName(mob.type, lvl)

                mob.setMetadata(MARKED_ENTITY_METADATA, FixedMetadataValue(ServerTweaks.plugin!!, lvl))

            }

            return null

        }

        fun addMobStacks(ent : LivingEntity, count : Int) {

            if(ent.hasMetadata(MARKED_ENTITY_METADATA)) {

                val origLevel = ent.getMetadata(MARKED_ENTITY_METADATA).first().asInt()
                val level = if(origLevel + count > MAX_MOB_STACK_LEVEL) MAX_MOB_STACK_LEVEL else origLevel + count

                ent.removeMetadata(MARKED_ENTITY_METADATA, ServerTweaks.plugin!!)
                ent.setMetadata(MARKED_ENTITY_METADATA, FixedMetadataValue(ServerTweaks.plugin!!, level))
                ent.customName = formatStackedMobName(ent.type, level)

            }

        }

        fun isStackedSpawner(block : Block) : Boolean {

            return getStackerEntity(block) != null

        }

        fun formatStackedSpawnerName(ent : EntityType, stack : Int) = "${ChatColor.GREEN}${ent.name.replace("_", " ")}${ChatColor.GOLD}${ChatColor.BOLD} x $stack"

        fun formatStackedMobName(ent : EntityType, lvl : Int) = "${ChatColor.GREEN}${ent.name.replace("_", " ")}${ChatColor.WHITE} Lvl.${ChatColor.YELLOW}$lvl"

        fun tagToSize(base : String, s : String) : Int {
            if(s.startsWith(base))
                return s.substringAfter('.').toInt()

            return -1
        }

        fun sizeToTag(stack : Int) : String = "$SCOREBOARD_TAG_STACKS.$stack"

        fun createStackingEntity(block : Block, stack : Int = 1) : ArmorStand? {

            if(!isStackedSpawner(block) && block.type == Material.SPAWNER) {

                val armorStand = block.world.spawnEntity(block.location.add(0.5, 1.0, 0.5), EntityType.ARMOR_STAND) as ArmorStand

                val entType = (block.state as CreatureSpawner).spawnedType

                armorStand.isVisible = false

                armorStand.isCustomNameVisible = true
                armorStand.customName = formatStackedSpawnerName(entType, stack)
                armorStand.addScoreboardTag(STACKED_ARMOR_STAND_SCOREBOARD_TAG)
                armorStand.addScoreboardTag(sizeToTag(stack))

                armorStand.isInvulnerable = true
                armorStand.isSmall = true
                armorStand.isMarker = true
                armorStand.setGravity(false)

                return armorStand

            }

            return null
        }

    }

    private var purchaseListener : SpawnerPurchaseSign = SpawnerPurchaseSign()

    override fun create() {
        ServerTweaks.plugin!!.registerAsListener(this)
        ServerTweaks.plugin!!.registerAsListener(purchaseListener)
        ServerTweaks.plugin!!.getCommand("givespawner")!!.setExecutor(GiveSpawnerCommand())
        ServerTweaks.plugin!!.getCommand("disablespawner")!!.setExecutor(DisableSpawnerCommand())
    }

    override fun close() {
        ServerTweaks.plugin!!.unregisterListener(this)
        ServerTweaks.plugin!!.unregisterListener(purchaseListener)
        ServerTweaks.plugin!!.getCommand("givespawner")!!.setExecutor(null)
        ServerTweaks.plugin!!.getCommand("disablespawner")!!.setExecutor(null)


        ServerTweaks.plugin!!.server.worlds.forEach {

            it.entities.forEach {iv ->

                if(iv.hasMetadata(MARKED_ENTITY_METADATA)) iv.remove()

            }

        }


    }

    @EventHandler
    fun silkTouchSpawnerEvent(event : BlockBreakEvent) {

        if(event.block.type == Material.SPAWNER) {

            val inHand = event.player.inventory.itemInMainHand

            if(inHand.containsEnchantment(Enchantment.SILK_TOUCH)) {

                val entityType = (event.block.state as CreatureSpawner).spawnedType
                event.isDropItems = false

                val size = getSpawnerStackSize(event.block)

                val itemDrop = createStackedSpawnerItem(entityType, size)

                event.block.world.dropItem(event.block.location.add(0.5, 0.0, 0.5), itemDrop)

            }

            getStackerEntity(event.block)?.remove()
            getStackingMob(event.block)?.remove()

        }

    }

    @EventHandler
    fun spawnerBlownUpBlock(event : BlockExplodeEvent) {

        for( b in event.blockList()) {

            if(b.type == Material.SPAWNER) {

                getStackerEntity(b)?.remove()
                getStackingMob(b)?.remove()

            }

        }

    }

    @EventHandler
    fun spawnerBlownUpEntity(event : EntityExplodeEvent) {

        for( b in event.blockList()) {

            if(b.type == Material.SPAWNER) {

                getStackerEntity(b)?.remove()
                getStackingMob(b)?.remove()

            }

        }

    }

    @EventHandler
    fun spawnerPlaceEvent(event : BlockPlaceEvent) {

        if(event.blockPlaced.type == Material.SPAWNER && !event.isCancelled) {

            val type = getEntityTypeForSpanwerItem(event.itemInHand)

            if(type != EntityType.UNKNOWN) {

                setSpawnerEntityType(event.block, type)

                val stackSize = NBTItemTagUtil.getTag(STACKED_NBT_KEY, event.itemInHand)?.toInt() ?: 1

                createStackingEntity(event.block, stackSize)

            }

        }

    }

    @EventHandler
    fun spawnerStackingEvent(event : PlayerInteractEvent) {
        if(!event.player.isSneaking && (event.action == Action.RIGHT_CLICK_BLOCK)) {

            val b = event.clickedBlock

            if (b != null && b.type == Material.SPAWNER) {//TODO build protection checking (?)

                val inHand = event.item
                if (inHand != null && inHand.type == Material.SPAWNER) {

                    val entHand = getEntityTypeForSpanwerItem(inHand) //THIS will create the stacked spawner entity if needed.
                    val entBlock = (b.state as CreatureSpawner).spawnedType

                    var stackSize = 1

                    if (entHand == entBlock) {

                        event.isCancelled = true

                        if (getSpawnerStackSize(b) >= SPAWNER_MAX_STACK_SIZE) {
                            event.player.sendMessage("${ChatColor.RED}This spawner is already at its maximum stack size ($SPAWNER_MAX_STACK_SIZE)!")
                            return
                        }

                        if (NBTItemTagUtil.hasTag(STACKED_NBT_KEY, inHand))
                            stackSize = NBTItemTagUtil.getTag(STACKED_NBT_KEY, inHand)!!.toInt()

                        //val stacked = NBTItemTagUtil.getTag("stacked", inHand)?.toInt()

                        val success = addSpawnerStacks(b, inHand.amount, stackSize)

                        if (!success.first) {
                            if(success.second == 0)
                                event.player.sendMessage("${ChatColor.RED}You cannot stack your targeted spawner because the spawner in your hand has too many stacks on it!")
                            else
                                inHand.amount = inHand.amount - success.second

                        } else {

                            when (event.hand) {
                                EquipmentSlot.HAND -> event.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                                EquipmentSlot.OFF_HAND -> event.player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                            }

                        }

                    }

                }

            }

        }

    }

    @EventHandler
    fun stackedMobDieEvent(event : EntityDeathEvent) {

        val mobSkull = HeadBounty.getMobSkullBounty(event.entityType) //add head bounty loots here
        if(mobSkull != null) event.drops.add(mobSkull)

        if(event.entity.hasMetadata(MARKED_ENTITY_METADATA)) {

            val currentLvl = event.entity.getMetadata(MARKED_ENTITY_METADATA).first().asInt()
            event.entity.customName = ""
            event.entity.isCustomNameVisible = false
            event.entity.remove()

            if(currentLvl > 1) {
                //event.isCancelled = true
                makeStackingMob(event.entity.world.getBlockAt(event.entity.location.add(0.0, -1.0, 0.0).toBlockLocation()), currentLvl-1)
                //event.drops.clear()
            }

        }

    }

    @EventHandler
    fun stackedSpawnerSpawningEvent(event : SpawnerSpawnEvent) {

        event.isCancelled = true

        if(isStackedSpawner(event.spawner.block) && !event.spawner.block.isEmpty) {
            //event.entity.setMetadata("stackedMultiplier", FixedMetadataValue(ServerTweaks.plugin!!, getSpawnerStackSize(event.spawner.block)))

            if(event.entity is LivingEntity) {

                val mob = getStackingMob(event.spawner.block)

                if(mob != null) {

                    addMobStacks(mob, getSpawnerStackSize(event.spawner.block))

                } else {

                    makeStackingMob(event.spawner.block, getSpawnerStackSize(event.spawner.block))

                }

            }

        }

    }


}