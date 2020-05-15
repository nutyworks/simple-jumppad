package me.nutyworks.simplejumppad

import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SimpleJumpPadPlugin() : JavaPlugin() {
    val playerJumpPadModeMap = mutableMapOf<Player, Boolean>()
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
    override fun onLoad() {
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
