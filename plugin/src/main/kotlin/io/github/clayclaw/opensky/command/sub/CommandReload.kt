package io.github.clayclaw.opensky.command.sub

import io.github.clayclaw.opensky.command.OpenSkyCommandBase
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.system.config.Config
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender

class CommandReload(
    override val sender: CommandSender,
    private val configMessage: Config<ConfigMessage>,
): OpenSkyCommandBase(
    commandName = "reload",
) {

    override fun run() {
        runBlocking {
            configMessage.reload()
        }
        sender.sendMessage(configMessage.content.reloaded())
    }

}