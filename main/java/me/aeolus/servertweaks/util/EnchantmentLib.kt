package me.aeolus.servertweaks.util

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object EnchantmentLib {

    enum class MaterialBundle(vararg val materials : Material) {
        PICKAXE(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE),
        AXE(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE),
        SHOVEL(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL),
        SWORD(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD),
        HELMET(Material.LEATHER_HELMET, Material.IRON_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.TURTLE_HELMET),
        CHESTPLATE(Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE),
        LEGGINGS(Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS),
        BOOTS(Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS),
        BOW(Material.BOW),
        TRIDENT(Material.TRIDENT),
        CROSSBOW(Material.CROSSBOW),
        FISHING_ROD(Material.FISHING_ROD),
        ELYTRA(Material.ELYTRA),
        SHIELD(Material.SHIELD)
    }

    fun getCorrespondingBundle(item : ItemStack) : MaterialBundle? {

        MaterialBundle.values().forEach { if(it.materials.contains(item.type)) return it }
        return null

    }

    private val enchantmentTable = HashMap<MaterialBundle, Array<Enchantment>>()

    init {

        enchantmentTable[EnchantmentLib.MaterialBundle.PICKAXE] = arrayOf(Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.MENDING, Enchantment.SILK_TOUCH, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.AXE] = arrayOf(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.MENDING, Enchantment.SILK_TOUCH, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.SHOVEL] = arrayOf(Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.MENDING, Enchantment.SILK_TOUCH, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.SWORD] = arrayOf(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD, Enchantment.FIRE_ASPECT, Enchantment.KNOCKBACK, Enchantment.LOOT_BONUS_MOBS, Enchantment.MENDING, Enchantment.SWEEPING_EDGE, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.HELMET] = arrayOf(Enchantment.WATER_WORKER, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.MENDING, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.OXYGEN, Enchantment.THORNS, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.CHESTPLATE] = arrayOf(Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.MENDING, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.THORNS, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.LEGGINGS] = arrayOf(Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.MENDING, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.THORNS, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.BOOTS] = arrayOf(Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.MENDING, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.THORNS, Enchantment.DURABILITY, Enchantment.FROST_WALKER, Enchantment.PROTECTION_FALL, Enchantment.DEPTH_STRIDER)
        enchantmentTable[EnchantmentLib.MaterialBundle.BOW] = arrayOf(Enchantment.ARROW_FIRE, Enchantment.ARROW_INFINITE, Enchantment.MENDING, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.TRIDENT] = arrayOf(Enchantment.CHANNELING, Enchantment.IMPALING, Enchantment.LOYALTY, Enchantment.MENDING, Enchantment.RIPTIDE, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.CROSSBOW] = arrayOf(Enchantment.MULTISHOT, Enchantment.PIERCING, Enchantment.QUICK_CHARGE, Enchantment.DURABILITY, Enchantment.MENDING)
        enchantmentTable[EnchantmentLib.MaterialBundle.FISHING_ROD] = arrayOf(Enchantment.LUCK, Enchantment.LURE, Enchantment.MENDING, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.ELYTRA] = arrayOf(Enchantment.MENDING, Enchantment.DURABILITY)
        enchantmentTable[EnchantmentLib.MaterialBundle.SHIELD] = arrayOf(Enchantment.MENDING, Enchantment.DURABILITY)

    }

    fun canBeEnchanted(item : ItemStack) = getCorrespondingBundle(item) != null

    fun getEnchants(item : ItemStack) : Array<Enchantment>? {

        val bundle = getCorrespondingBundle(item)

        return if(bundle != null) getEnchants(bundle) else null

    }

    /*
    this gets rid of all enchantments that will conflict with each other if they're already on the item
     */
    fun getSanitizedEnchantments(item : ItemStack) : Array<Enchantment>? {

        val bundle = getCorrespondingBundle(item)

        return if(bundle != null) {

            getSanitizedEnchantments(bundle, item.enchantments)

        } else null



    }

    fun getSanitizedEnchantments(bundle : MaterialBundle, enchantMap : Map<Enchantment, Int>) : Array<Enchantment> {

        if(enchantMap.isEmpty()) return getEnchants(bundle)
        else {

            val enchants = enchantMap.keys.toList()

            val unsanitized = getEnchants(bundle)

            val sanitized = unsanitized.toMutableList()

            for(en1 in unsanitized) {
                    for(en2 in enchants) {

                        if(en1.conflictsWith(en2) && en1 != en2) sanitized.remove(en1)
                        else if(en1 == en2 && enchantMap[en1] == en2.maxLevel) sanitized.remove(en1)

                    }
                }

                return sanitized.toTypedArray()
        }
    }

    fun getEnchants(bundle : MaterialBundle) : Array<Enchantment> = enchantmentTable[bundle]!!

    fun makeEnchantmentBook(enchant : Enchantment, level : Int, customName : String? = null, customLore : List<String>? = null) : ItemStack {

        val itemBase = ItemStack(Material.ENCHANTED_BOOK)

        val enchantMeta = itemBase.itemMeta as EnchantmentStorageMeta

        enchantMeta.addStoredEnchant(enchant, level, false)

        if(customName != null) enchantMeta.setDisplayName(customName)
        if(customLore != null) enchantMeta.lore = customLore

        itemBase.itemMeta = enchantMeta

        return itemBase

    }

    fun prettyEnchantName(e : Enchantment) : String = StringUtil.romanCapitilizationSentence(e.key.key.replace("_", " "))


}