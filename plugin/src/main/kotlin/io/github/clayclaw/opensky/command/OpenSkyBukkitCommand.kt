package io.github.clayclaw.opensky.command

import com.github.ajalt.clikt.core.NoSuchOption
import com.github.ajalt.clikt.core.subcommands
import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.command.sub.CommandReload
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.data.provider.PartyManager
import io.github.clayclaw.opensky.system.config.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class OpenSkyBukkitCommand: CommandExecutor, KoinComponent {

    private val plugin: OpenSkyPlugin by inject()
    private val partyManager: PartyManager by inject()
    private val configMessage: Config<ConfigMessage> by inject(named("messages"))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        runCatching {
            CommandOpenSky(sender)
                .subcommands(
                    CommandReload(sender, configMessage),
                )
                .parse(args?.toList() ?: emptyList())
        }.onFailure { throwable ->
            when(throwable) {
                is NoSuchOption -> {
                    sender.sendMessage(configMessage.content.commandOptionsDoesNotExists())
                }
                else -> {
                    configMessage.content.commandPrintHelp().forEach { sender.sendMessage(it) }
                }
            }
        }
        return true
    }

}