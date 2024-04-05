package io.github.clayclaw.opensky.command

import com.github.ajalt.clikt.core.NoOpCliktCommand
import org.bukkit.command.CommandSender

abstract class OpenSkyCommandBase(
    commandName: String? = null,
): NoOpCliktCommand(name = commandName) {
    abstract val sender: CommandSender
}