package io.github.clayclaw.opensky.command.sub

import io.github.clayclaw.opensky.command.OpenSkyCommandBase
import org.bukkit.command.CommandSender

class CommandParty(
    override val sender: CommandSender,
): OpenSkyCommandBase(
    commandName = "party",
) {

    override fun run() {
        sender.sendMessage("Hello, Party!")
    }

}