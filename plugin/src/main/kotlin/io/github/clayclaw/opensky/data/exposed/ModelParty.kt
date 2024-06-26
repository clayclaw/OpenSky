package io.github.clayclaw.opensky.data.exposed

import io.github.clayclaw.opensky.party.ImmutableParty
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import java.util.*

object TableParties: BaseIdTable<UUID>("opensky_parties") {
    val uuid = uuid("uuid").autoGenerate()
    val leaderUUID = uuid("leaderUUID")
    val name = varchar("name", 255)

    override val id: Column<EntityID<UUID>> = uuid.entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}

class EntityParty(id: EntityID<UUID>): BaseEntity<UUID>(id, TableParties) {
    companion object: BaseEntityClass<UUID, EntityParty>(TableParties)

    val uuid by TableParties.uuid
    val name by TableParties.name
    val leaderUUID by TableParties.leaderUUID
    val members by EntityPartyMember via TablePartyMembers
}

object TablePartyMembers: BaseIdTable<Int>("opensky_party_players") {
    val party = reference("party", TableParties)
    val playerUUID = uuid("playerUUID")

    override val id: Column<EntityID<Int>> = integer("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)
}

class EntityPartyMember(id: EntityID<Int>): BaseEntity<Int>(id, TablePartyMembers) {
    companion object: BaseEntityClass<Int, EntityPartyMember>(TablePartyMembers)

    val playerUUID by TablePartyMembers.playerUUID
    val party by EntityParty referencedOn TablePartyMembers.party
}

fun EntityParty.toImmutable(): ImmutableParty {
    return ImmutableParty(
        uuid = uuid,
        name = name,
        leader = leaderUUID,
        members = members.map { it.playerUUID }.toSet()
    )
}