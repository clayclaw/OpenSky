package io.github.clayclaw.opensky.event

import io.github.clayclaw.opensky.island.Island
import org.bukkit.event.HandlerList

class IslandCreateEvent(island: Island) : IslandEvent(island) {

    companion object {
        private val HANDLER_LIST = HandlerList()
        @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    override fun getHandlers() = HANDLER_LIST

}