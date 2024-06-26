package io.github.clayclaw.opensky.island

import io.github.clayclaw.opensky.party.ImmutableParty
import io.github.clayclaw.opensky.party.MutableParty
import io.github.clayclaw.opensky.party.Party
import org.bukkit.World
import java.util.*

sealed class Island(
    val uuid: UUID,
    val party: Party,
    var name: String?,
) {

    val worldName = "sky-${uuid}"

    class Unloaded(
        uuid: UUID,
        party: ImmutableParty,
        name: String?,
    ): Island(uuid, party, name)

    class Local(
        uuid: UUID,
        party: MutableParty,
        name: String?,
        val world: World,
    ): Island(uuid, party, name)

    class Remote(
        uuid: UUID,
        party: ImmutableParty,
        name: String?,
        val worldData: RemoteIslandWorldData,
    ): Island(uuid, party, name)

}

data class RemoteIslandWorldData(
    val serverId: String,
    val worldName: String,
    val onlinePlayerIds: Set<RemoteIslandPlayerData>,
)

data class RemoteIslandPlayerData(
    val uuid: UUID,
    val displayName: String,
)