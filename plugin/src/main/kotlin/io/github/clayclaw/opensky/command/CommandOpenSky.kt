package io.github.clayclaw.opensky.command

import org.bukkit.command.CommandSender

class CommandOpenSky(
    override val sender: CommandSender,
): OpenSkyCommandBase() {

    override fun run() {
        sender.sendMessage("Hello, OpenSky!")
    }

}