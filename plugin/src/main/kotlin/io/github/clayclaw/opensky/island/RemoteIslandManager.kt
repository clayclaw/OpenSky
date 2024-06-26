package io.github.clayclaw.opensky.island

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.config.BaseConfig
import io.github.clayclaw.opensky.data.messaging.MessageRemoteIslandCreated
import io.github.clayclaw.opensky.data.messaging.MessageRemoteIslandUnloaded
import io.github.clayclaw.opensky.data.messaging.MessageServerKeepalive
import io.github.clayclaw.opensky.data.messaging.MessageServerShutdown
import io.github.clayclaw.opensky.system.network.PubSubNetworkService
import io.github.clayclaw.opensky.system.network.publish
import io.github.clayclaw.opensky.system.network.subscribe
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.event.Listener
import org.koin.core.annotation.Single
import java.util.LinkedList
import java.util.UUID
import java.util.logging.Logger

@Single
class RemoteIslandManager(
    private val plugin: OpenSkyPlugin,
    private val networkService: PubSubNetworkService,
    private val baseConfig: BaseConfig,
    private val logger: Logger,
): Listener {

    private val cancellableJobs = LinkedList<Job>()
    private val serverMap = HashMap<String, RemoteServer>()
    private val islandMap = HashMap<UUID, Island.Remote>()

    internal fun init() {
        // If a remote server sends a message that an island is created, update the island data
        networkService.subscribe<MessageRemoteIslandCreated> {
            getRemoteServer(it.serverId).islandUUIDs.add(it.islandUUID)
            islandMap[it.islandUUID] = it.islandState
        }

        // If a remote server sends a message that an island is unloaded, remove the island data
        networkService.subscribe<MessageRemoteIslandUnloaded> {
            getRemoteServer(it.serverId).islandUUIDs.remove(it.islandUUID)
            islandMap.remove(it.islandUUID)
        }

        // If a remote server sends a keepalive message, update the last alive timestamp
        networkService.subscribe<MessageServerKeepalive> {
            getRemoteServer(it.serverId).lastAliveTimestamp = it.timestamp
        }

        // If a remote server goes down, remove all islands associated with it
        networkService.subscribe<MessageServerShutdown> {
            serverMap.remove(it.serverId)
        }

        // Periodically send keepalive messages to all remote servers
        plugin.launch(plugin.asyncDispatcher) {
            while(isActive) {
                delay(baseConfig.system.serverKeepaliveTimeMillis / 2)
                networkService.publish(MessageServerKeepalive(baseConfig.serverId, System.currentTimeMillis()))
            }
        }.also { cancellableJobs.add(it) }

        // Periodically check if any remote servers are down, remove remote islands associated with them if they're down
        plugin.launch {
            while(isActive) {
                delay(baseConfig.system.serverKeepaliveTimeMillis / 2)
                val now = System.currentTimeMillis()
                serverMap.values
                    .filter { now - it.lastAliveTimestamp > baseConfig.system.serverKeepaliveTimeMillis }
                    .map { it.serverId }
                    .toList()
                    .forEach { serverId ->
                        serverMap[serverId]?.islandUUIDs?.forEach { islandMap.remove(it) }
                        serverMap.remove(serverId)
                        logger.warning("Server $serverId is probably down, removing all remote island data associated with it")
                    }
            }
        }.also { cancellableJobs.add(it) }

        plugin.server.pluginManager.registerSuspendingEvents(this, plugin)
    }

    internal fun close() {
        plugin.launch {
            cancellableJobs.forEach { it.cancelAndJoin() }
            cancellableJobs.clear()
        }
    }

    private fun getRemoteServer(serverId: String): RemoteServer {
        return serverMap.getOrPut(serverId) { RemoteServer(serverId, System.currentTimeMillis()) }
    }

    fun getAllRemoteIslands(): List<Island.Remote> {
        return islandMap.values.toList()
    }

    fun isRemoteIslandExists(islandUUID: UUID): Boolean {
        return islandMap.containsKey(islandUUID)
    }

    fun getRemoteIsland(islandUUID: UUID): Island.Remote? {
        return islandMap[islandUUID]
    }

    data class RemoteServer(
        val serverId: String,
        var lastAliveTimestamp: Long,
        val islandUUIDs: HashSet<UUID> = HashSet()
    )

}