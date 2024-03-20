package io.github.clayclaw.opensky.models

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import java.util.*

object Islands: BaseIdTable<UUID>("opensky_islands") {
    val uuid = uuid("uuid").autoGenerate()
    val name = varchar("name", 256).nullable()
    val party = reference("party", Parties)

    override val id: Column<EntityID<UUID>> = uuid.entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(Parties.id) }
}

class Island(id: EntityID<UUID>): BaseEntity<UUID>(id, Islands) {
    companion object: BaseEntityClass<UUID, Island>(Islands)

    val uuid by Islands.uuid
    val name by Islands.name
    var party by Party referencedOn Islands.party
}