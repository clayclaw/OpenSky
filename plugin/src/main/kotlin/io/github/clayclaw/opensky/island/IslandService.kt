package io.github.clayclaw.opensky.island

import org.bukkit.World
import java.util.*

interface IslandService {

    /**
     * Check if the world is an local island world.
     */
    fun isIslandWorld(world: World): Boolean

    /**
     * Get the local island by the world.
     */
    fun getLocalIslandByWorld(world: World): Island.Local?

    /**
     * Check if the island is loaded locally by the UUID.
     */
    fun isIslandLoadedLocally(islandUUID: UUID): Boolean

    /**
     * Get the local island by the UUID.
     */
    fun getLocalIsland(islandUUID: UUID): Island.Local?

    /**
     * Check if the island is created in the data source.
     */
    suspend fun isIslandPresent(islandUUID: UUID): Boolean

    /**
     * Get the island information.
     * This function should firstly check if the island is loaded locally
     * If not, it should look for the remote island data in the cache
     * Finally, it should determine if the island is present in the data source
     */
    suspend fun getIsland(islandUUID: UUID): Island?

    /**
     * Get all local islands.
     */
    fun getAllLocalIslands(): List<Island.Local>

    /**
     * Get all islands in the local server and remote servers.
     */
    suspend fun getAllIslands(): List<Island>

    /**
     * Create a new island and load it.
     */
    suspend fun createNewIsland(): Island.Local

    /**
     * Load the island from the data source.
     */
    suspend fun loadIsland(islandUUID: UUID): Island.Local

}