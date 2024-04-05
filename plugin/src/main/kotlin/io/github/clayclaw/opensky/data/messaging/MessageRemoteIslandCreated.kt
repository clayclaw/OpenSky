package io.github.clayclaw.opensky.data.messaging

import io.github.clayclaw.opensky.island.Island
import java.util.*

data class MessageRemoteIslandCreated(
    override val serverId: String,
    override val timestamp: Long,
    override val islandUUID: UUID,
    val islandState: Island.Remote,
): IslandNetworkMessage