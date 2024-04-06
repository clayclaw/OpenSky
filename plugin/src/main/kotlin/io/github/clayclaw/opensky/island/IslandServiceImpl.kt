package io.github.clayclaw.opensky.island

import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.config.BaseConfig
import io.github.clayclaw.opensky.config.ConfigMessage
import io.github.clayclaw.opensky.data.exposed.EntityIsland
import io.github.clayclaw.opensky.data.exposed.EntityParty
import io.github.clayclaw.opensky.data.exposed.toUnloadedIsland
import io.github.clayclaw.opensky.event.IslandCreatedEvent
import io.github.clayclaw.opensky.event.IslandDeletedEvent
import io.github.clayclaw.opensky.event.IslandLoadedEvent
import io.github.clayclaw.opensky.extension.callEvent
import io.github.clayclaw.opensky.island.loader.ASWMIslandLoader
import io.github.clayclaw.opensky.island.loader.IslandLoader
import io.github.clayclaw.opensky.island.loader.IslandLoaders
import io.github.clayclaw.opensky.party.Party
import io.github.clayclaw.opensky.party.PartyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.World
import org.koin.core.annotation.Single
import java.util.*
import java.util.logging.Logger

@Single
class IslandServiceImpl(
    private val plugin: OpenSkyPlugin,
    private val remoteIslandManager: RemoteIslandManager,
    private val baseConfig: BaseConfig,
    private val configMessage: ConfigMessage,
    private val partyManager: PartyManager,
    private val logger: Logger,
): IslandService {

    private val localIslands = HashMap<UUID, Island.Local>()
    private val localIslandBukkitWorldMapping = HashMap<UUID, Island.Local>()

    private val islandLoader: IslandLoader by lazy {
        when(baseConfig.islandWorldLoader.uppercase()) {
            IslandLoaders.ASWM.name -> ASWMIslandLoader(plugin, partyManager)
            else -> {
                logger.severe("Unknown island world loader: ${baseConfig.islandWorldLoader}")
                throw IllegalArgumentException("Unknown island world loader: ${baseConfig.islandWorldLoader}")
            }
        }
    }

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
        if(isIslandLoaded(islandUUID)) return true
        return withContext(Dispatchers.IO) {
            EntityIsland.findById(islandUUID) != null
        }
    }

    override suspend fun getIsland(islandUUID: UUID): Island? {
        if(isIslandLoadedLocally(islandUUID)) return getLocalIsland(islandUUID)
        if(remoteIslandManager.isRemoteIslandExists(islandUUID)) return remoteIslandManager.getRemoteIsland(islandUUID)!!
        return withContext(Dispatchers.IO) {
            EntityIsland.findById(islandUUID)?.toUnloadedIsland()
        }
    }

    override fun getAllLocalIslands(): List<Island.Local> {
        return localIslands.values.toList()
    }

    override suspend fun getAllIslands(): List<Island> {
        return remoteIslandManager.getAllRemoteIslands() + localIslands.values
    }

    override suspend fun createNewIsland(party: Party): Island.Local {
        val islandUUID = UUID.randomUUID()
        val entityIsland = withContext(Dispatchers.IO) {
            val entityParty = EntityParty.findById(party.uuid) ?: throw IllegalStateException("Party not found")
            EntityIsland.new(islandUUID) {
                this.party = entityParty
                name = configMessage.defaultIslandName
            }
        }

        val unloadedIsland = entityIsland.toUnloadedIsland()
        callEvent(IslandCreatedEvent(unloadedIsland))
        return loadIsland(unloadedIsland)
    }

    override suspend fun loadIsland(islandUUID: UUID): Island.Local {
        val unloadedIsland = withContext(Dispatchers.IO) {
            EntityIsland.findById(islandUUID)?.toUnloadedIsland() ?: throw IllegalStateException("Island not found")
        }
        return loadIsland(unloadedIsland)
    }

    private suspend fun loadIsland(island: Island.Unloaded): Island.Local {
        requireIslandNotLoaded(island.uuid)

        val loadedIsland = islandLoader.loadIsland(island)
        localIslands[island.uuid] = loadedIsland
        localIslandBukkitWorldMapping[loadedIsland.world.uid] = loadedIsland

        callEvent(IslandLoadedEvent(loadedIsland))
        return loadedIsland
    }

    override suspend fun unloadIsland(island: Island.Local): Island.Unloaded {
        if(!isIslandLoadedLocally(island.uuid)) throw IllegalStateException("Island not loaded")
        return islandLoader.unloadIsland(island)
    }

    override suspend fun deleteIsland(islandUUID: UUID) {
        requireIslandNotLoaded(islandUUID)

        val entityIsland = withContext(Dispatchers.IO) {
            (EntityIsland.findById(islandUUID) ?: throw IllegalStateException("Island not found")).also {
                it.delete()
            }
        }
        val unloadedIsland = entityIsland.toUnloadedIsland()
        islandLoader.deleteIsland(unloadedIsland)

        callEvent(IslandDeletedEvent(unloadedIsland))
    }

    private fun isIslandLoaded(islandUUID: UUID): Boolean {
        return isIslandLoadedLocally(islandUUID) || remoteIslandManager.isRemoteIslandExists(islandUUID)
    }

    private fun requireIslandNotLoaded(islandUUID: UUID) {
        if(isIslandLoadedLocally(islandUUID)) throw IllegalStateException("Island already loaded")
        if(remoteIslandManager.isRemoteIslandExists(islandUUID)) throw IllegalStateException("Island already loaded in remote server")
    }

}