package io.github.clayclaw.opensky.command.sub

import com.github.ajalt.clikt.core.NoOpCliktCommand
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.system.config.Config
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender

class CommandReload(
    private val sender: CommandSender,
    private val configMessage: Config<ConfigMessage>,
): NoOpCliktCommand(
    name = "reload",
) {

    override fun run() {
        runBlocking {
            configMessage.reload()
        }
        sender.sendMessage(configMessage.content.reloaded())
    }

}