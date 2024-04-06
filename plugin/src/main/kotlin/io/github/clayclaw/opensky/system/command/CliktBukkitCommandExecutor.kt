package io.github.clayclaw.opensky.system.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoSuchOption
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.system.exception.command.CommandSenderNoPermissionException
import io.github.clayclaw.opensky.system.exception.command.CommandSenderTypeNotMatchException
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

abstract class CliktBukkitCommandExecutor: CommandExecutor {

    abstract val configMessage: ConfigMessage
    abstract fun compose(sender: CommandSender): CliktCommand

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        runCatching {
            compose(sender)
                .parse(args?.toList() ?: emptyList())
        }.onFailure { throwable ->
            when(throwable) {
                is NoSuchOption -> {
                    sender.sendMessage(configMessage.commandOptionsDoesNotExists())
                }
                is CommandSenderNoPermissionException -> {
                    sender.sendMessage(configMessage.commandNoPermission())
                }
                is CommandSenderTypeNotMatchException -> {
                    when(throwable.requiredType) {
                        is Player -> {
                            sender.sendMessage(configMessage.commandSenderCanOnlyBePlayer())
                        }
                        is ConsoleCommandSender -> {
                            sender.sendMessage(configMessage.commandSenderCanOnlyBeConsole())
                        }
                        else -> {
                            sender.sendMessage(configMessage.commandInvalid())
                        }
                    }
                }
                else -> {
                    sender.sendMessage(configMessage.commandInvalid())
                }
            }
        }
        return true
    }

}