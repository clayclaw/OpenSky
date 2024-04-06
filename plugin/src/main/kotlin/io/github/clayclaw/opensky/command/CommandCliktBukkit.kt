package io.github.clayclaw.opensky.command

import io.github.clayclaw.opensky.system.command.CliktBukkitCommandBase
import org.bukkit.command.CommandSender

class CommandCliktBukkit(
    override val sender: CommandSender,
): CliktBukkitCommandBase() {

    override fun run() {
        sender.sendMessage("Hello, OpenSky!")
    }

}