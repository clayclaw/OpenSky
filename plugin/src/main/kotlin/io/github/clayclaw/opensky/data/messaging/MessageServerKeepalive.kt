package io.github.clayclaw.opensky.data.messaging

data class MessageServerKeepalive(
    override val serverId: String,
    override val timestamp: Long,
): NetworkMessage
