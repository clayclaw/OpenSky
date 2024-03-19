package io.github.clayclaw.opensky.extension

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.COLOR_CHAR
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.translateFullChatColor() = translateHexColor().let { ChatColor.translateAlternateColorCodes('&', it) }

fun String.translateHexColor() = translateHexColorCodes("#")

fun String.translateHexColorCodes(startTag: String): String {
    val hexPattern: Pattern = Pattern.compile("$startTag([A-Fa-f0-9]{6})")
    val matcher: Matcher = hexPattern.matcher(this)
    val buffer = StringBuffer(this.length + 4 * 8)
    while (matcher.find()) {
        val group: String = matcher.group(1)
        matcher.appendReplacement(
            buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group[0] + COLOR_CHAR + group[1]
                    + COLOR_CHAR + group[2] + COLOR_CHAR + group[3]
                    + COLOR_CHAR + group[4] + COLOR_CHAR + group[5]
        )
    }
    return matcher.appendTail(buffer).toString()
}