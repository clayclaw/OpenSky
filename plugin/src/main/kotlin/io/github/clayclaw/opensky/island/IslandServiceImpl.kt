package io.github.clayclaw.opensky.island

import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.party.Party
import org.bukkit.World
import org.koin.core.annotation.Single
import java.util.*
import kotlin.collections.HashMap

@Single
class IslandServiceImpl(
    private val plugin: OpenSkyPlugin,
): IslandService {

    private val localIslands = HashMap<UUID, Island.Local>()
    private val localIslandBukkitWorldMapping = HashMap<UUID, Island.Local>()

    override fun isIslandWorld(world: World): Boolean {
        return localIslandBukkitWorldMapping.containsKey(world.uid)
    }

    override fun getLocalIslandByWorld(world: World): Island.Local? {
        return localIslandBukkitWorldMapping[world.uid]
    }

    override fun isIslandLoadedLocally(islandUUID: UUID): Boolean {
        return localIslands.containsKey(islandUUID)
    }

    override fun getLocalIsland(islandUUID: UUID): Island.Local? {
        return localIslands[islandUUID]
    }

    override suspend fun isIslandPresent(islandUUID: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getIsland(islandUUID: UUID): Island? {
        TODO("Not yet implemented")
    }

    override fun getAllLocalIslands(): List<Island.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllIslands(): List<Island> {
        TODO("Not yet implemented")
    }

    override suspend fun createNewIsland(party: Party): Island.Local {
        TODO("Not yet implemented")
    }

    override suspend fun loadIsland(islandUUID: UUID): Island.Local {
        TODO("Not yet implemented")
    }

    override suspend fun deleteIsland(islandUUID: UUID) {
        TODO("Not yet implemented")
    }

}