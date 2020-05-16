package me.nutyworks.simplejumppad

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class JumpPadEditGUI(val loc: Location) {

    val inv = Bukkit.createInventory(null, 9, "§*Jump pad @ ${loc.blockX},${loc.blockY},${loc.blockZ}")

    fun open(player: Player) {
        val jumpPadMode = SimpleJumpPadPlugin.instance.jumpPadConfig.getString("${loc.blockX},${loc.blockY},${loc.blockZ}.mode")
        when (jumpPadMode) {
            "disabled" -> {
                inv.setItem(2, ItemStack(Material.BARRIER).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName("${ChatColor.RED}Disabled")
                        lore = mutableListOf("${ChatColor.GRAY}Click to change to facing.")
                    }
                })
                inv.setItem(6, ItemStack(Material.BARRIER).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName(" ")
                    }
                })
                SimpleJumpPadPlugin.instance.jumpPadConfig.set("${loc.blockX},${loc.blockY},${loc.blockZ}.mode", "disabled")
            }
            "facing" -> {
                inv.setItem(2, ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName("${ChatColor.GREEN}Facing")
                        lore = mutableListOf("${ChatColor.GRAY}Click to change to vector.")
                    }
                })
                inv.setItem(4, ItemStack(Material.LIME_DYE).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName("${ChatColor.GREEN}Set horizontal")

                        val hr = SimpleJumpPadPlugin.instance.jumpPadConfig.getVector("${loc.blockX},${loc.blockY},${loc.blockZ}.vector")!!.x
                        lore = mutableListOf("${ChatColor.GRAY}Value: $hr")
                    }
                })
                inv.setItem(6, ItemStack(Material.LIME_DYE).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName("${ChatColor.GREEN}Set vertical")

                        val vt = SimpleJumpPadPlugin.instance.jumpPadConfig.getVector("${loc.blockX},${loc.blockY},${loc.blockZ}.vector")!!.y
                        lore = mutableListOf("${ChatColor.GRAY}Value: $vt")
                    }
                })
                SimpleJumpPadPlugin.instance.jumpPadConfig.set("${loc.blockX},${loc.blockY},${loc.blockZ}.mode", "facing")
            }
            "vector" -> {
                inv.setItem(2, ItemStack(Material.ARROW).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName("${ChatColor.GREEN}Vector")
                        lore = mutableListOf("${ChatColor.GRAY}Click to change to disabled.")
                    }
                })
                inv.setItem(6, ItemStack(Material.LIME_DYE).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName("${ChatColor.GREEN}Set vector")

                        val vc = SimpleJumpPadPlugin.instance.jumpPadConfig.getVector("${loc.blockX},${loc.blockY},${loc.blockZ}.vector")!!
                        lore = mutableListOf("${ChatColor.GRAY}Value: (%.2f, %.2f, %.2f)".format(vc.x, vc.y, vc.z))
                    }
                })
                SimpleJumpPadPlugin.instance.jumpPadConfig.set("${loc.blockX},${loc.blockY},${loc.blockZ}.mode", "vector")
            }
        }
        player.openInventory(inv)
    }
}