package io.github.clayclaw.opensky.island.template

import org.bukkit.World

interface IslandTemplate {

    suspend fun createNewWorld(): World

}