package me.nutyworks.simplejumppad

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class JumpPadModifyListener(private val plugin: SimpleJumpPadPlugin) : Listener {
    @EventHandler
    fun onJumpPadModify(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK || !e.player.isSneaking) return

        val clickedBlock = e.clickedBlock ?: return

        if ("pressure_plate" !in clickedBlock.type.key.key) return

        val loc = clickedBlock.location
        val isJumpPadPlate = plugin.jumpPadConfig.getKeys(false).contains("${loc.blockX},${loc.blockY},${loc.blockZ}")
        if (!isJumpPadPlate) return

        if (e.hand == EquipmentSlot.OFF_HAND) return

        if (!e.player.isOp) return

        JumpPadEditGUI(loc).open(e.player)
        e.player.playSound(e.player.location, Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f)

        e.isCancelled = true
    }

    @EventHandler
    fun onJumpPadVectorModify(e: PlayerInteractEvent) {
        if (e.player !in plugin.playerJumpPadEditType.filter { k -> k.value.second == EditJumpPad.VECTOR }.keys) return
        if (e.action !in setOf(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK)) return
        if (e.hand == EquipmentSlot.OFF_HAND) return

        val loc = plugin.playerJumpPadEditType[e.player]!!.first

        when (e.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {

                val padLocation = plugin.playerJumpPadEditType[e.player]!!.first.clone().add(0.5, 0.0, 0.5)
                val targetLocation = e.player.eyeLocation.clone().add(e.player.location.direction.multiply(2))
                val changedVector = targetLocation.clone().subtract(padLocation).toVector()

                plugin.jumpPadConfig.set("${loc.blockX},${loc.blockY},${loc.blockZ}.vector", changedVector)
                plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)

                e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)

                plugin.server.scheduler.runTaskLater(plugin, { ->
                    JumpPadEditGUI(loc).open(e.player)
                }, 1)
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                e.player.playSound(e.player.location, Sound.BLOCK_ANVIL_PLACE, 1f, 1f)

                plugin.server.scheduler.runTaskLater(plugin, { ->
                    JumpPadEditGUI(loc).open(e.player)
                }, 1)
            }
            else -> e.player.sendMessage("How did you get here?")
        }

        plugin.playerJumpPadEditType.remove(e.player)

        e.isCancelled = true

//        targetLocation.clone().subtract(padLocation).toVector()
    }

    @EventHandler
    fun onJumpPadValueChat(e: AsyncPlayerChatEvent) {
        val editType = plugin.playerJumpPadEditType[e.player] ?: return
        if (editType.second !in setOf(EditJumpPad.FACING_HORIZONTAL, EditJumpPad.FACING_VERTICAL)) return

        val value = e.message.toDoubleOrNull() ?: return e.player.sendMessage("${ChatColor.RED}'${e.message}' is not a number.")

        val loc = editType.first

        val changedVector = plugin.jumpPadConfig.getVector("${loc.blockX},${loc.blockY},${loc.blockZ}.vector")!!.apply {
            if (editType.second == EditJumpPad.FACING_HORIZONTAL) x = value
            else if (editType.second == EditJumpPad.FACING_VERTICAL) y = value
        }

        plugin.jumpPadConfig.set("${loc.blockX},${loc.blockY},${loc.blockZ}.vector", changedVector)
        plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)

        plugin.playerJumpPadEditType.remove(e.player)

        plugin.server.scheduler.runTaskLater(plugin, { ->
            JumpPadEditGUI(loc).open(e.player)
            e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        }, 1)

        e.isCancelled = true
    }
}
