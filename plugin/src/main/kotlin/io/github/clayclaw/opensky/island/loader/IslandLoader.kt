package io.github.clayclaw.opensky.island.loader

import io.github.clayclaw.opensky.island.Island
import org.bukkit.World

interface IslandLoader {

    /**
     * Import a world as a template for island first time creation.
     */
    suspend fun importWorldTemplate(world: World): Island.Unloaded

    /**
     * Load island from data source.
     */
    suspend fun loadIsland(islandId: Island.Unloaded): Island.Local

    /**
     * Save island to data source.
     */
    suspend fun saveIsland(island: Island.Local)

    /**
     * Unload island from bukkit.
     */
    suspend fun unloadIsland(island: Island.Local)

    /**
     * Delete island from data source.
     */
    suspend fun deleteIsland(island: Island.Unloaded)

}