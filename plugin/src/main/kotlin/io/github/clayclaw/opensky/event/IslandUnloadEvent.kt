package io.github.clayclaw.opensky.event

import io.github.clayclaw.opensky.island.LocalIsland
import org.bukkit.event.HandlerList

class IslandUnloadEvent(
    island: LocalIsland
) : IslandEvent(island) {

    companion object {
        private val HANDLER_LIST = HandlerList()
        @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    override fun getHandlers() = HANDLER_LIST

}