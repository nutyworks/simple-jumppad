package me.nutyworks.simplejumppad

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class JumpPadEditGUIListener(private val plugin: SimpleJumpPadPlugin) : Listener {
    @EventHandler
    fun onJumpPadEditGuiClick(e: InventoryClickEvent) {
        if (!e.view.title.startsWith("§*Jump pad @ ")) return

        val loc = e.view.title.split("@ ")[1]
        val jumpPadMode = plugin.jumpPadConfig.getString("$loc.mode")

        val locSplit = loc.split(",")
        val realLoc = Location(e.whoClicked.world, locSplit[0].toDouble(), locSplit[1].toDouble(), locSplit[2].toDouble())

        if (e.clickedInventory?.holder == null) {
            when (jumpPadMode) {
                "disabled" -> {
                    when (e.slot) {
                        2 -> {
                            plugin.jumpPadConfig.set("$loc.mode", "facing")
                            plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)
                        }
                    }
                }
                "facing" -> {
                    when (e.slot) {
                        2 -> {
                            plugin.jumpPadConfig.set("$loc.mode", "vector")
                            plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)
                        }
                        4 -> {
                            e.whoClicked.closeInventory()
                            e.whoClicked.sendMessage("${ChatColor.GREEN}Horizontal velocity (type in chat)")
                            plugin.playerJumpPadEditType[e.whoClicked as Player] = Pair(realLoc, EditJumpPad.FACING_HORIZONTAL)
                        }
                        6 -> {
                            e.whoClicked.closeInventory()
                            e.whoClicked.sendMessage("${ChatColor.GREEN}Vertical velocity (type in chat)")
                            plugin.playerJumpPadEditType[e.whoClicked as Player] = Pair(realLoc, EditJumpPad.FACING_VERTICAL)
                        }
                    }
                }
                "vector" -> {
                    when (e.slot) {
                        2 -> {
                            plugin.jumpPadConfig.set("$loc.mode", "disabled")
                            plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)
                        }
                        3 -> {
                            e.whoClicked.closeInventory()
                            e.whoClicked.sendMessage("${ChatColor.GREEN}Vector X (type in chat)")
                            plugin.playerJumpPadEditType[e.whoClicked as Player] = Pair(realLoc, EditJumpPad.VECTOR_X)
                        }
                        4 -> {
                            e.whoClicked.closeInventory()
                            e.whoClicked.sendMessage("${ChatColor.GREEN}Vector Y (type in chat)")
                            plugin.playerJumpPadEditType[e.whoClicked as Player] = Pair(realLoc, EditJumpPad.VECTOR_Y)
                        }
                        5 -> {
                            e.whoClicked.closeInventory()
                            e.whoClicked.sendMessage("${ChatColor.GREEN}Vector Z (type in chat)")
                            plugin.playerJumpPadEditType[e.whoClicked as Player] = Pair(realLoc, EditJumpPad.VECTOR_Z)
                        }
                        6 -> {
                            e.whoClicked.closeInventory()
                            e.whoClicked.sendMessage("${ChatColor.GREEN}Vector (left-click to apply, right-click to cancel)")
                            plugin.playerJumpPadEditType[e.whoClicked as Player] = Pair(realLoc, EditJumpPad.VECTOR_ALL)
                        }
                    }
                }
            }
        }

        if (e.slot == 2) {
            JumpPadEditGUI(realLoc).open(e.whoClicked as Player)
            (e.whoClicked as Player).playSound(e.whoClicked.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        }

        e.isCancelled = true
    }
}

enum class EditJumpPad {
    NONE, FACING_HORIZONTAL, FACING_VERTICAL, VECTOR_ALL, VECTOR_X, VECTOR_Y, VECTOR_Z
}
