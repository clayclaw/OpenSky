package io.github.clayclaw.opensky.data.messaging

data class MessageServerShutdown(
    override val serverId: String,
    override val timestamp: Long,
): NetworkMessage
