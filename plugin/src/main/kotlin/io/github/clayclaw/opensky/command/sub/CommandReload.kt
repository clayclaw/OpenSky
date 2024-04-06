package io.github.clayclaw.opensky.command.sub

import io.github.clayclaw.opensky.system.command.CliktBukkitCommandBase
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.system.config.Config
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender

class CommandReload(
    override val sender: CommandSender,
    private val configMessage: Config<ConfigMessage>,
): CliktBukkitCommandBase(
    commandName = "reload",
) {

    override fun run() {
        requirePermission("opensky.command.reload")
        runBlocking {
            configMessage.reload()
        }
        sender.sendMessage(configMessage.content.reloaded())
    }

}