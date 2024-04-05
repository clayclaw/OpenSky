package io.github.clayclaw.opensky.data.messaging

import java.util.UUID

interface NetworkMessage {
    val serverId: String
    val timestamp: Long
}

interface IslandNetworkMessage: NetworkMessage {
    val islandUUID: UUID
}