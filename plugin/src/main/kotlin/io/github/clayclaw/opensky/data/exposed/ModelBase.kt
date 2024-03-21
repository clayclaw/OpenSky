package io.github.clayclaw.opensky.data.exposed

import io.github.clayclaw.opensky.extension.currentUTC
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.jodatime.datetime

abstract class BaseIdTable<T : Comparable<T>>(name: String): IdTable<T>(name) {
    val createdAt = datetime("createdAt").clientDefault { currentUTC() }
    val updatedAt = datetime("updatedAt").clientDefault { currentUTC() }
}

abstract class BaseEntity<T : Comparable<T>>(id: EntityID<T>, table: BaseIdTable<T>) : Entity<T>(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseEntityClass<T: Comparable<T>, E: BaseEntity<T>>(table: BaseIdTable<T>): EntityClass<T, E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                runCatching {
                    action.toEntity(this)?.updatedAt = currentUTC()
                }
            }
        }
    }
}