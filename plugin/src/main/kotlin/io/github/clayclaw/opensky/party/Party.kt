package io.github.clayclaw.opensky.party

import java.util.UUID

interface Party {

    val uuid: UUID
    var name: String?

    val leader: UUID
    val members: Set<UUID>

    fun addMember(member: UUID)
    fun removeMember(member: UUID)

    fun switchLeader(leader: UUID)

    fun disband()

}