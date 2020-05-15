package me.nutyworks.simplejumppad

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

        if (e.clickedInventory?.holder == null) {
            when (e.slot) {
                2 -> when (jumpPadMode) {
                    "disabled" -> {
                        plugin.jumpPadConfig.set("$loc.mode", "facing")
                        plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)
                    }
                    "facing" -> {
                        plugin.jumpPadConfig.set("$loc.mode", "vector")
                        plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)
                    }
                    "vector" -> {
                        plugin.jumpPadConfig.set("$loc.mode", "disabled")
                        plugin.jumpPadConfig.save(plugin.jumpPadConfigFile)
                    }
                }
            }
        }

        JumpPadEditGUI(plugin.jumpPadConfig.getLocation("$loc.location")!!).open(e.whoClicked as Player)

        e.isCancelled = true
    }
}
