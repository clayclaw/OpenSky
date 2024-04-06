package io.github.clayclaw.opensky.extension

import org.bukkit.Bukkit
import org.bukkit.event.Event

fun callEvent(event: Event) {
    Bukkit.getServer().pluginManager.callEvent(event)
}