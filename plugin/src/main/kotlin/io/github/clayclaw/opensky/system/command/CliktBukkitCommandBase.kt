package io.github.clayclaw.opensky.system.command

import com.github.ajalt.clikt.core.NoOpCliktCommand
import io.github.clayclaw.opensky.system.exception.command.CommandSenderNoPermissionException
import io.github.clayclaw.opensky.system.exception.command.CommandSenderTypeNotMatchException
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class CliktBukkitCommandBase(
    commandName: String? = null,
): NoOpCliktCommand(name = commandName) {
    abstract val sender: CommandSender

    fun requirePermission(permission: String) {
        if (!sender.hasPermission(permission)) {
            throw CommandSenderNoPermissionException(sender, permission)
        }
    }

    fun requirePlayerSender() {
        if (sender !is Player) {
            throw CommandSenderTypeNotMatchException(Player::class)
        }
    }

    fun player() = sender as Player

}