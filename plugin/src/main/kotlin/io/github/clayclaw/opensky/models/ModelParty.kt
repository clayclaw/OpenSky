package io.github.clayclaw.opensky.models

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import java.util.*

object Parties: BaseIdTable<UUID>("opensky_parties") {
    val uuid = uuid("uuid").autoGenerate()
    val leaderUUID = uuid("leaderUUID")

    override val id: Column<EntityID<UUID>> = uuid.entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}

class Party(id: EntityID<UUID>): BaseEntity<UUID>(id, Parties) {
    companion object: BaseEntityClass<UUID, Party>(Parties)

    val uuid by Parties.uuid
    val leaderUUID by Parties.leaderUUID
    val members by PartyMember via PartyMembers
}

object PartyMembers: BaseIdTable<Int>("opensky_party_players") {
    val party = reference("party", Parties)
    val playerUUID = uuid("playerUUID")

    override val id: Column<EntityID<Int>> = integer("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)
}

class PartyMember(id: EntityID<Int>): BaseEntity<Int>(id, PartyMembers) {
    companion object: BaseEntityClass<Int, PartyMember>(PartyMembers)

    val playerUUID by PartyMembers.playerUUID
    val party by Party referencedOn PartyMembers.party
}