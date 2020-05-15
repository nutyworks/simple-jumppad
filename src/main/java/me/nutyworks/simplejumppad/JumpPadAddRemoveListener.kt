package me.nutyworks.simplejumppad

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.util.Vector

class JumpPadAddRemoveListener(private val plugin: SimpleJumpPadPlugin) : Listener {

    @EventHandler
    fun onJumpPadPlace(e: BlockPlaceEvent) {
        if ("pressure_plate" !in e.block.type.key.key) return
        if (!plugin.playerJumpPadModeMap.getOrPut(e.player, { -> false })) return

        val loc = e.block.location

        plugin.jumpPadConfig["${loc.blockX},${loc.blockY},${loc.blockZ}"] =
                JumpPadData(loc, JumpPadData.Mode.FACING, Vector(1, 1, 0))
        plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)

        e.player.sendMessage("Jump pad placed at ${loc.blockX},${loc.blockY},${loc.blockZ}")
    }

    @EventHandler
    fun onJumpPadRemove(e: BlockBreakEvent) {
        if ("pressure_plate" !in e.block.type.key.key) return

        val loc = e.block.location
        val isJumpPadPlate = plugin.jumpPadConfig.getKeys(false).contains("${loc.blockX},${loc.blockY},${loc.blockZ}")
        if (!isJumpPadPlate) return

        plugin.jumpPadConfig["${loc.blockX},${loc.blockY},${loc.blockZ}"] = null
        plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)

        e.player.sendMessage("Removed jump pad at ${loc.blockX},${loc.blockY},${loc.blockZ}")
    }
}