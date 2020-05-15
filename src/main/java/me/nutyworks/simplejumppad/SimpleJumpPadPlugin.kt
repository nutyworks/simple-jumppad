package me.nutyworks.simplejumppad

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.File

class SimpleJumpPadPlugin() : JavaPlugin() {
    val playerJumpPadModeMap = mutableMapOf<Player, Boolean>()
    val jumpPadLocation = mutableMapOf<Location, JumpPadData>()
    lateinit var jumpPadConfig: YamlConfiguration

    val jumpPadConfigFile = File(dataFolder, "jump_pads.yml")

    override fun onEnable() {
        server.pluginManager.registerEvents(JumpPadAddRemoveListener(this), this)
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
    }

    private fun loadJumpPadConfig() {
        if (!dataFolder.exists())
            dataFolder.mkdirs()
        if (!jumpPadConfigFile.exists())
            jumpPadConfigFile.createNewFile()

        jumpPadConfig = YamlConfiguration.loadConfiguration(File(dataFolder, "jump_pads.yml"))

        println("jump_pads.yml loaded! $jumpPadConfig")
    }
}

data class JumpPadData(val location: Location, val mode: Mode, val vector: Vector) : ConfigurationSerializable {
    enum class Mode {
        FACING, VECTOR
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(Pair("location", location), Pair("mode", mode), Pair("vector", vector))
    }
}
