package me.aeolus.servertweaks.util

import com.mojang.authlib.GameProfile
import net.minecraft.server.v1_14_R1.EntityPlayer
import net.minecraft.server.v1_14_R1.PlayerInteractManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import java.util.*

object FakePlayerUtil {


    fun spawnNewFakePlayer(loc : Location, displayName : String) : EntityPlayer {

        val mcServer = (Bukkit.getServer() as CraftServer).server
        val craftWorld = (loc.world as CraftWorld).handle

        val gProfile = GameProfile(UUID.randomUUID(), displayName)

        val npc = EntityPlayer(mcServer, craftWorld, gProfile, PlayerInteractManager(craftWorld))

        npc.setLocation(loc.x, loc.y, loc.z, loc.pitch, loc.yaw)

        //definitely needs testing LOL

        return npc
    }

}