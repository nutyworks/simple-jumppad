package me.nutyworks.simplejumppad

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

class JumpPadUseListener(private val plugin: SimpleJumpPadPlugin) : Listener {
    @EventHandler
    fun onPlayerJumpPadUse(e: PlayerInteractEvent) {
        if (e.action != Action.PHYSICAL) return

        val clickedBlock = e.clickedBlock ?: return

        if ("pressure_plate" !in clickedBlock.type.key.key) return

        val loc = clickedBlock.location
        val isJumpPadPlate = plugin.jumpPadConfig.getKeys(false).contains("${loc.blockX},${loc.blockY},${loc.blockZ}")
        if (!isJumpPadPlate) return

        val jumpPadMode = plugin.jumpPadConfig.getString("${loc.blockX},${loc.blockY},${loc.blockZ}.mode")!!
        val jumpPadVector = plugin.jumpPadConfig.getVector("${loc.blockX},${loc.blockY},${loc.blockZ}.vector")!!

        when (jumpPadMode) {
            "facing" -> {
                e.player.velocity = e.player.location.direction.clone().apply { y = 0.0 }
                        .normalize().multiply(jumpPadVector.x)
                        .add(Vector(.0, jumpPadVector.y, .0))
            }
            "vector" -> {
                e.player.velocity = jumpPadVector
            }
        }
    }
}