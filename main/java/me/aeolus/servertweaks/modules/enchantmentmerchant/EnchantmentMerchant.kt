package me.aeolus.servertweaks.modules.enchantmentmerchant

import me.aeolus.servertweaks.ServerTweaks
import me.aeolus.servertweaks.modules.Module
import me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons.ButtonAccept
import me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons.ButtonCurrentEconomy
import me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons.ButtonReturn
import me.aeolus.servertweaks.modules.enchantmentmerchant.guibuttons.ButtonSelectEnchantment
import me.aeolus.servertweaks.modules.headbounty.HeadBounty
import me.aeolus.servertweaks.util.*
import me.aeolus.servertweaks.util.EnchantmentLib.prettyEnchantName
import me.aeolus.servertweaks.util.gui.GUIButton
import me.aeolus.servertweaks.util.gui.GUIPage
import me.aeolus.servertweaks.util.gui.GUISession
import me.aeolus.servertweaks.util.gui.GUISessionManager
import me.aeolus.servertweaks.util.gui.genericButtons.StationaryButton
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class EnchantmentMerchant : Module, Listener, CommandExecutor {

    companion object {

        const val GUI_TAG_SELECTED_ITEM = "__selected__"
        const val GUI_TAG_SELECTED_LEVEL = "__level__"
        const val GUI_TAG_SELECTOR_PAGE = "selectorPage"
        const val GUI_TAG_SELECTED_ENCHANTMENT = "__enchant__"
        const val MERCHANT_LOCATION_TAG = "__mercLocation__"

        val VILLAGER_NAME = "${ChatColor.BLUE}${ChatColor.BOLD}Enchantment Merchant"

        val MAIN_PAGE_ENCHANT_LORE = listOf("${ChatColor.WHITE}Select this book to look at all available levels", "${ChatColor.WHITE}for the corresponding enchantment.")
        val MAIN_PAGE_NO_ENCHANT_LORE = listOf("${ChatColor.DARK_GRAY}Your item has already maxed out this enchantment!")

        val enchantmentCosts = HashMap<Enchantment, HashMap<Int, Int>>()

    }

    override fun onCommand(s: CommandSender, c: Command, lbl: String, args: Array<out String>): Boolean {

        if(args.isNotEmpty()) {

            if(args[0].toLowerCase() == "setlocation" && s is Player) {

                merchantLocation = s.location

                getMerchantEntity().teleport(merchantLocation)
            }

        }

        return true
    }

    private lateinit var merchantLocation : Location
    private var merchantEntity : Villager? = null

    override fun create() {

        ServerTweaks.plugin!!.registerAsListener(this)
        ServerTweaks.plugin!!.getCommand("enchanter")!!.setExecutor(this)

        val conf = ServerTweaks.config!!

        if(conf.contains("enchantmentmerchant.costs")) {

            for( entry in conf.getConfigurationSection("enchantmentmerchant.costs")!!.getValues(false) ) {

                val enchant = Enchantment.getByName(entry.key)!!
                val costs = HashMap<Int, Int>()

                for( e2 in conf.getConfigurationSection("enchantmentmerchant.costs.${entry.key}")!!.getValues(false) )
                    costs[e2.key.toInt()] = e2.value as Int

                enchantmentCosts[enchant] = costs

            }

        } else {

            val serializedCosts = HashMap<String, HashMap<Int, Int>>()

            for( e in Enchantment.values() ) {

                val costs = HashMap<Int, Int>()

                for( i in 1..e.maxLevel )
                    costs[i] = 10

                enchantmentCosts[e] = costs
                serializedCosts[e.name] = costs

            }

            conf.set("enchantmentmerchant.costs", serializedCosts)

            ServerTweaks.plugin!!.saveConfig()


        }

        val serializedLocation = HashMap<String, Any>()

        Bukkit.getScheduler().scheduleSyncDelayedTask(ServerTweaks.plugin!!, {
            if (ServerTweaks.plugin!!.config.contains("enchantmentmerchant.location")) {

                for (entry in ServerTweaks.plugin!!.config.getConfigurationSection("enchantmentmerchant.location")!!.getValues(
                    false
                )) {

                    serializedLocation[entry.key] = entry.value

                }

                merchantLocation = Location.deserialize(serializedLocation)
                makeMerchantEntity()

            }
        }, 100)

    }

    override fun close() {

        ServerTweaks.plugin!!.unregisterListener(this)
        ServerTweaks.plugin!!.getCommand("enchanter")!!.setExecutor(null)

        ServerTweaks.plugin!!.config.set("enchantmentmerchant.location", merchantLocation.serialize())
        ServerTweaks.plugin!!.saveConfig()

        removeMerchantEntity()

    }

    @EventHandler
    fun playerInteract(event : PlayerInteractEntityEvent) {

        if(event.rightClicked == merchantEntity) {

            val inHand = event.player.inventory.getItem(EquipmentSlot.HAND)

            event.isCancelled = true
            if(inHand != null) {
                val bundle = EnchantmentLib.getCorrespondingBundle(inHand)

                if(bundle != null) {

                    val enchants = EnchantmentLib.getSanitizedEnchantments(bundle, inHand.enchantments).toMutableList()
                    enchants.remove(Enchantment.MENDING)

                    if(enchants.isEmpty()) {

                        event.player.sendMessage("${ChatColor.RED}This item can't be enchanted anymore!")
                        rejectionAnimation(event.player)

                        return

                    }

                    /**
                     * session creation
                     */

                    val pages = HashMap<String, GUIPage>()
                    val homepage = GUIPage((ceil(enchants.size / 9.0) + 1).toInt(), name = "${ChatColor.BLUE}${ChatColor.BOLD}Enchantment Merchant")
                    val selectorPage = GUIPage(2, name = "${ChatColor.GREEN}${ChatColor.BOLD}Select Level for ${prettyEnchantName(Enchantment.SILK_TOUCH)}")

                    val fullEnchants = ArrayList<Enchantment>()
                    val availableEnchants = ArrayList<Enchantment>()

                    for( e in inHand.enchantments ) if(e.key.maxLevel == e.value) fullEnchants.add(e.key)
                    for( e in enchants ) if(!fullEnchants.contains(e)) availableEnchants.add(e)

                    for(e in fullEnchants) {

                        val itemBase = ItemStack(Material.BEDROCK)
                        val itemMeta = itemBase.itemMeta

                        itemMeta.addEnchant(e, e.maxLevel, true)
                        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}${prettyEnchantName(e)} is maxed out!")
                        itemMeta.lore = MAIN_PAGE_NO_ENCHANT_LORE

                        itemBase.itemMeta = itemMeta

                        val lockedSubPage = StationaryButton(itemBase)

                        homepage.putButton(button = lockedSubPage)

                    }

                    for(e in availableEnchants) {

                        val subpageButton = ButtonSelectEnchantment(
                            EnchantmentLib.makeEnchantmentBook(e, 1, "${ChatColor.BLUE}${ChatColor.BOLD}${prettyEnchantName(e)} Enchantments", MAIN_PAGE_ENCHANT_LORE),
                            e
                        )

                        homepage.putButton(button = subpageButton)

                    }

                    val itemNamed = inHand.clone()
                    val itemMeta = itemNamed.itemMeta
                    itemMeta.setDisplayName("${ChatColor.YELLOW}${ChatColor.BOLD}Selected Item")
                    itemNamed.itemMeta = itemMeta

                    homepage.putButton(homepage.size - 5, StationaryButton(itemNamed))

                    homepage.putButton(homepage.size - 1, ButtonCurrentEconomy(event.player))


                    pages[GUISession.IDENTIFIER_MAIN_PAGE] = homepage
                    pages[GUI_TAG_SELECTOR_PAGE] = selectorPage

                    val session = GUISession(event.player, pages)
                    GUISessionManager.addSession<EnchantmentMerchant>(event.player, session)
                    session.internalDataStream[GUI_TAG_SELECTED_ITEM] = inHand
                    session.internalDataStream[MERCHANT_LOCATION_TAG] = merchantLocation

                    event.player.playSound(merchantLocation, Sound.ENTITY_VILLAGER_CELEBRATE, 0.7F, 1.0F)

                } else {

                    event.player.sendMessage("${ChatColor.RED}This item can't be enchanted!")
                    rejectionAnimation(event.player)

                }

            } else {

                event.player.sendMessage("${ChatColor.RED}Right-click the merchant with the item you wish to enchant!")
                rejectionAnimation(event.player)

            }

        }

    }

    fun makeMerchantEntity() {

        if(merchantEntity == null) merchantEntity = merchantLocation.world.spawnEntity(merchantLocation, EntityType.VILLAGER) as Villager

        merchantEntity!!.setAI(false)
        merchantEntity!!.canPickupItems = false
        merchantEntity!!.setAdult()
        merchantEntity!!.profession = Villager.Profession.CLERIC
        merchantEntity!!.villagerType = Villager.Type.TAIGA
        merchantEntity!!.isInvulnerable = true

        merchantEntity!!.isCustomNameVisible = true
        merchantEntity!!.customName = VILLAGER_NAME

    }

    fun getMerchantEntity() : Villager {

        if(merchantEntity == null) makeMerchantEntity()
        return merchantEntity!!

    }

    fun removeMerchantEntity() {

        if(merchantEntity != null) {

            merchantEntity!!.remove()
            merchantEntity = null

            for( ent in merchantLocation.getNearbyEntitiesByType(Villager::class.java, 5.0))
                if(!ent.hasAI() && ent.name == VILLAGER_NAME) ent.remove()

        }

    }

    fun completedAnimation(p : Player) {

        if(merchantEntity != null) {

            p.playSound(merchantLocation, Sound.ENTITY_VILLAGER_WORK_CLERIC, 1.0F, 1.0F)

            val particleRunner = object : AnimatedParticleSession(p) {

                var piCounter = 0.0
                var rad = 2.0
                val initialLocation = merchantLocation.clone()
                val maxPi = PI * 8

                override fun run() {

                    if(p.world != merchantLocation.world){
                        cancel()
                        return
                    }
                    if(piCounter >= maxPi){
                        particle(Particle.FIREWORKS_SPARK, p.location, 20, 0.08, 0.08, 0.08, 2.0)
                        p.playSound(p.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
                        cancel()
                        return
                    }

                    val prog = piCounter / maxPi

                    val playLocation = lerpLocation(initialLocation, p.location, prog).add(cos(piCounter) * rad, 0.0, sin(piCounter) * rad)

                    particle(Particle.CRIT_MAGIC, playLocation, 1, 0.01, 0.01, 0.01)

                    rad = if(rad < 0.05) 0.0 else rad - 0.05
                    piCounter += PI/8

                }

            }.runTaskTimer(ServerTweaks.plugin!!, 0, 1)


        }

    }

    fun rejectionAnimation(p : Player) {
        if(merchantEntity != null) {
            p.spawnParticle(Particle.VILLAGER_ANGRY, merchantEntity!!.eyeLocation, 5, 0.5, 0.0, 0.5)
            p.playSound(merchantLocation, Sound.ENTITY_VILLAGER_NO, 0.7F, 1.0F)
        }
    }


}