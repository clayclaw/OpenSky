package io.github.clayclaw.opensky.data.exposed

import io.github.clayclaw.opensky.island.Island
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import java.util.*

object TableIslands: BaseIdTable<UUID>("opensky_islands") {
    val uuid = uuid("uuid").autoGenerate()
    val name = varchar("name", 256).nullable()
    val party = reference("party", TableParties)

    override val id: Column<EntityID<UUID>> = uuid.entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(TableParties.id) }
}

class EntityIsland(id: EntityID<UUID>): BaseEntity<UUID>(id, TableIslands) {
    companion object: BaseEntityClass<UUID, EntityIsland>(TableIslands)

    var uuid by TableIslands.uuid
    var name by TableIslands.name
    var party by EntityParty referencedOn TableIslands.party
}

fun EntityIsland.toUnloadedIsland(): Island.Unloaded {
    return Island.Unloaded(uuid, party.toImmutable(), name)
}