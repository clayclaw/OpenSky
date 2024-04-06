package io.github.clayclaw.opensky.config

import io.github.clayclaw.opensky.extension.translateFullChatColor

data class ConfigMessage(
    var prefix: String = "&6[OpenSky] ",

    var reloaded: String = "#00FFFFConfig reloaded successfully, if you're making changes to database or cache config please restart the server instead",

    var commandOptionsDoesNotExists: String = "#00FFFFNo such command option",
    var commandNoPermission: String = "#00FFFFYou don't have permission to do that",
    var commandPrintHelp: List<String> = listOf(
        "#00FFFF",
    ),
    var commandSenderCanOnlyBePlayer: String = "#00FFFFCommand sender can only be player",
    var commandSenderCanOnlyBeConsole: String = "#00FFFFCommand sender can only be console",
    var commandInvalid: String = "#00FFFFInvalid command, use &7/opensky help #00FFFFfor more information",

    var defaultIslandName: String = "Default Island"
) {

    fun reloaded() = "${prefix}${reloaded}".translateFullChatColor()
    fun commandOptionsDoesNotExists() = "${prefix}${commandOptionsDoesNotExists}".translateFullChatColor()
    fun commandNoPermission() = "${prefix}${commandNoPermission}".translateFullChatColor()
    fun commandPrintHelp() = commandPrintHelp.map { it.translateFullChatColor() }
    fun commandSenderCanOnlyBePlayer() = "${prefix}${commandSenderCanOnlyBePlayer}".translateFullChatColor()
    fun commandSenderCanOnlyBeConsole() = "${prefix}${commandSenderCanOnlyBeConsole}".translateFullChatColor()
    fun commandInvalid() = "${prefix}${commandInvalid}".translateFullChatColor()

}