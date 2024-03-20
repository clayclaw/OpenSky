package io.github.clayclaw.opensky.island.loader

import io.github.clayclaw.opensky.island.Island
import java.io.File

interface IslandLoader {

    /**
     * Import a world as a template for island first time creation.
     */
    suspend fun importWorldTemplate(worldFolder: File)

    /**
     * Load island from data source.
     */
    suspend fun loadIsland(island: Island.Unloaded): Island.Local

    /**
     * Save island to data source.
     */
    suspend fun saveIsland(island: Island.Local)

    /**
     * Unload island from bukkit.
     */
    suspend fun unloadIsland(island: Island.Local): Island.Unloaded

    /**
     * Delete island from data source.
     */
    suspend fun deleteIsland(island: Island.Unloaded)

}