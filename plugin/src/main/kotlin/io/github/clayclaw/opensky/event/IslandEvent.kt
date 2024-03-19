package io.github.clayclaw.opensky.event

import io.github.clayclaw.opensky.island.Island
import org.bukkit.event.Event

abstract class IslandEvent(
    val island: Island,
): Event()