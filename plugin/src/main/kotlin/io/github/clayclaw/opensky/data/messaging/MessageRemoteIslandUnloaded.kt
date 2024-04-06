package io.github.clayclaw.opensky.data.messaging

import java.util.*

data class MessageRemoteIslandUnloaded(
    override val serverId: String,
    override val timestamp: Long,
    override val islandUUID: UUID,
): IslandNetworkMessage
