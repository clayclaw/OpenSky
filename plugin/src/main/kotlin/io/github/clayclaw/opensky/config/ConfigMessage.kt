package io.github.clayclaw.opensky.config

import io.github.clayclaw.opensky.extension.translateFullChatColor

data class ConfigMessage(
    var prefix: String = "&6[OpenSky] ",

    var reloaded: String = "#00FFFFConfig reloaded successfully, if you're making changes to database or cache config please restart the server instead",

    var commandOptionsDoesNotExists: String = "#00FFFFNo such command option",
    var commandNoPermission: String = "#00FFFFYou don't have permission to do that",
    var commandPrintHelp: List<String> = listOf(
        "#00FFFFUsage: /opensky [subcommand] [options]",
    ),

    var defaultIslandName: String = "Default Island"
) {

    fun reloaded() = "${prefix}${reloaded}".translateFullChatColor()
    fun commandOptionsDoesNotExists() = "${prefix}${commandOptionsDoesNotExists}".translateFullChatColor()
    fun commandNoPermission() = "${prefix}${commandNoPermission}".translateFullChatColor()
    fun commandPrintHelp() = commandPrintHelp.map { it.translateFullChatColor() }

}