package me.aeolus.servertweaks.util

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

abstract class AnimatedParticleSession(val p : Player) : BukkitRunnable() {

    fun particle(part : Particle, loc : Location, count : Int, offX : Double = 0.0, offY : Double = 0.0, offZ : Double = 0.0, speed : Double = 0.0) = p.spawnParticle(part, loc, count, offX, offY, offZ, speed)

    fun lerp(i : Double, f : Double, prog : Double) = (i * (1-prog)) + (f * (prog))

    fun lerpLocation(li : Location, lf : Location, prog : Double) = Location(li.world, lerp(li.x, lf.x, prog), lerp(li.y, lf.y, prog), lerp(li.z, lf.z, prog))

    //TODO there are more variances... will add as needed.


    abstract override fun run()


}