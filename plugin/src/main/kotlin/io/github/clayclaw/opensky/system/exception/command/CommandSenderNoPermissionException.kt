package io.github.clayclaw.opensky.system.exception.command

import org.bukkit.command.CommandSender

class CommandSenderNoPermissionException(
    val sender: CommandSender,
    val permission: String,
): CommandException("Command sender ${sender.name} has no permission $permission")