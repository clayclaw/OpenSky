package io.github.clayclaw.opensky.party

import java.util.*

interface Party {

    val uuid: UUID
    var name: String?

    val leader: UUID
    val members: Set<UUID>

}

interface MutableParty: Party {

    fun addMember(member: UUID)
    fun removeMember(member: UUID)

    fun switchLeader(leader: UUID)

    fun disband()

}

class ImmutableParty(
    override val uuid: UUID,
    override var name: String?,
    override val leader: UUID,
    override val members: Set<UUID>
): Party {

    companion object {
        fun from(party: Party) = ImmutableParty(party.uuid, party.name, party.leader, party.members)
    }

}