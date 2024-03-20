package io.github.clayclaw.opensky.island.loader

import com.infernalsuite.aswm.api.SlimePlugin
import com.infernalsuite.aswm.api.loaders.SlimeLoader
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import io.github.clayclaw.opensky.island.Island
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import java.io.File
import java.lang.IllegalStateException

/**
 * Island Loader using Advanced Slime World Manager (ASWM)
 */
class ASWMIslandLoader(
    private val aswm: SlimePlugin,
    private val aswmLoader: SlimeLoader = aswm.getLoader("mysql"),
): IslandLoader {

    override suspend fun importWorldTemplate(worldFolder: File) {
        withContext(Dispatchers.IO) {
            aswm.importWorld(worldFolder, worldFolder.nameWithoutExtension, aswmLoader)
        }
    }

    // Enhancement: consider lift the slime property map
    override suspend fun loadIsland(island: Island.Unloaded): Island.Local {
        val slimeWorldFormat = withContext(Dispatchers.IO) {
            aswm.loadWorld(aswmLoader, island.worldName, false, SlimePropertyMap())
        }
        aswm.loadWorld(slimeWorldFormat)
        val bukkitWorld = Bukkit.getWorld(island.worldName)
            ?: throw IllegalStateException("Bukkit world not found after slime world loaded")
        return Island.Local(island.uuid, island.party, island.name, bukkitWorld)
    }

    override suspend fun saveIsland(island: Island.Local) {
        island.world.save()
    }

    override suspend fun unloadIsland(island: Island.Local): Island.Unloaded {
        val bukkitWorld = Bukkit.getWorld(island.worldName)
            ?: throw IllegalStateException("Local island cannot find bukkit world: ${island.uuid}")
        Bukkit.unloadWorld(bukkitWorld, true)
        return Island.Unloaded(island.uuid, island.party, island.name)
    }

    override suspend fun deleteIsland(island: Island.Unloaded) {
        withContext(Dispatchers.IO) {
            aswmLoader.deleteWorld(island.worldName)
        }
    }

}