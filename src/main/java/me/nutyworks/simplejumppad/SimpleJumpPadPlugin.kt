package me.nutyworks.simplejumppad

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SimpleJumpPadPlugin() : JavaPlugin() {
    val playerJumpPadModeMap = mutableMapOf<Player, Boolean>()
    val playerJumpPadEditType = mutableMapOf<Player, Pair<Location, EditJumpPad>>()
    val editingVector = ArrayList<Player>()

    lateinit var jumpPadConfig: YamlConfiguration

    val jumpPadConfigFile = File(dataFolder, "jump_pads.yml")

    companion object {
        lateinit var instance: SimpleJumpPadPlugin
    }

    override fun onEnable() {
        instance = this

        server.pluginManager.registerEvents(JumpPadAddRemoveListener(this), this)
        server.pluginManager.registerEvents(JumpPadModifyListener(this), this)
        server.pluginManager.registerEvents(JumpPadEditGUIListener(this), this)
        server.pluginManager.registerEvents(JumpPadUseListener(this), this)

        getCommand("togglejumppad")?.setExecutor { sender, _, _, _ ->
            if (sender is Player) {
                val isJumpPadMode = !playerJumpPadModeMap.getOrDefault(sender, false)
                playerJumpPadModeMap[sender] = isJumpPadMode

                sender.sendMessage("${ChatColor.GOLD}Jump-pad mode turned ${if (isJumpPadMode) "on" else "off"}.")
            } else {
                sender.sendMessage("${ChatColor.RED}This command is for players.")
            }

            return@setExecutor true
        }

        loadJumpPadConfig()

        server.scheduler.runTaskTimer(this, { ->
            for (player in editingVector) {
                val padLocation = playerJumpPadEditType[player]!!.first.clone().add(0.5, 0.0, 0.5)
                val targetLocation = player.eyeLocation.clone().add(player.location.direction.multiply(2))

                val distance = padLocation.distance(targetLocation).toInt()

                for (i in 0..distance) {
                    val particleLocation= padLocation.clone().add(targetLocation.clone().subtract(padLocation).toVector().normalize().multiply(i).toLocation(player.world))
                    player.spawnParticle(
                            Particle.REDSTONE, particleLocation, 1,
                            Particle.DustOptions(Color.fromBGR(0, 255, 0), 1f))
                }

            }
        }, 0, 20)
    }

    override fun onDisable() {
        jumpPadConfig.save(jumpPadConfigFile)
    }

    private fun loadJumpPadConfig() {

        if (!dataFolder.exists())
            dataFolder.mkdirs()
        if (!jumpPadConfigFile.exists())
            jumpPadConfigFile.createNewFile()

        jumpPadConfig = YamlConfiguration.loadConfiguration(jumpPadConfigFile)

        println("jump_pads.yml loaded! $jumpPadConfig")
    }
}
