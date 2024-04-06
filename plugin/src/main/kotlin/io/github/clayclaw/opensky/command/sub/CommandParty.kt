package io.github.clayclaw.opensky.command.sub

import io.github.clayclaw.opensky.system.command.CliktBukkitCommandBase
import org.bukkit.command.CommandSender

class CommandParty(
    override val sender: CommandSender,
): CliktBukkitCommandBase(
    commandName = "party",
) {

    override fun run() {
        sender.sendMessage("Hello, Party!")
    }

}