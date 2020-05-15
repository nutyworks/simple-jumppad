package me.nutyworks.simplejumppad

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class JumpPadModifyListener : Listener {
    @EventHandler
    fun onJumpPadModify(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK || !e.player.isSneaking) return
    }
}