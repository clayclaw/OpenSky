package io.github.clayclaw.opensky.event

import io.github.clayclaw.opensky.island.Island
import org.bukkit.event.HandlerList

/**
 * This event is fired after the island is loaded
 */
class IslandLoadedEvent(
    island: Island.Local
) : IslandEvent(island) {

    companion object {
        private val HANDLER_LIST = HandlerList()
        @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    override fun getHandlers() = HANDLER_LIST

}