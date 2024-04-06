package io.github.clayclaw.opensky.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.command.sub.CommandReload
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.party.PartyManager
import io.github.clayclaw.opensky.system.command.CliktBukkitCommandExecutor
import io.github.clayclaw.opensky.system.config.Config
import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class OpenSkyBukkitCommand: CliktBukkitCommandExecutor(), KoinComponent {

    private val plugin: OpenSkyPlugin by inject()
    private val partyManager: PartyManager by inject()
    private val configMessageWrapped: Config<ConfigMessage> by inject(named("messages"))

    override val configMessage: ConfigMessage by lazy { configMessageWrapped.content }

    override fun compose(sender: CommandSender): CliktCommand {
        return CommandCliktBukkit(sender)
            .subcommands(
                CommandReload(sender, configMessageWrapped),
            )
    }

}