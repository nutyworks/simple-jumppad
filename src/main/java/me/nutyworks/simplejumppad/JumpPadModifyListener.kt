package me.nutyworks.simplejumppad

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class JumpPadModifyListener(private val plugin: SimpleJumpPadPlugin) : Listener {
    @EventHandler
    fun onJumpPadModify(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK || !e.player.isSneaking) return

        if (e.clickedBlock == null) return
        val clickedBlock = e.clickedBlock!!

        if ("pressure_plate" !in clickedBlock.type.key.key) return

        val loc = clickedBlock.location
        val isJumpPadPlate = plugin.jumpPadConfig.getKeys(false).contains("${loc.blockX},${loc.blockY},${loc.blockZ}")
        if (!isJumpPadPlate) return

        if (e.hand == EquipmentSlot.OFF_HAND) return

        e.player.sendMessage(System.currentTimeMillis().toString())

//        e.player.sendMessage("${plugin.jumpPadConfig.getLocation("${loc.blockX},${loc.blockY},${loc.blockZ}.location")}")
    }
}