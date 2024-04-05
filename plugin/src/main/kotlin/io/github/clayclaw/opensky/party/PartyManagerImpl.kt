package io.github.clayclaw.opensky.party

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.data.exposed.EntityParty
import io.github.clayclaw.opensky.data.exposed.TableParties
import io.github.clayclaw.opensky.data.exposed.TablePartyMembers
import io.github.clayclaw.opensky.system.cache.RedisCacheService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import java.util.*

@Single
class PartyManagerImpl(
    private val plugin: OpenSkyPlugin,
    private val redisCacheService: RedisCacheService,
): PartyManager {

    companion object {
        const val KEY_PREFIX = "opensky-party"
        fun hashKey(key: UUID) = "$KEY_PREFIX:${key}"
        fun memberKey(key: UUID) = "$KEY_PREFIX:${key}:members"
    }

    fun createParty(leader: UUID, name: String?): MutableParty {
        val party = PartyDataProviderManagedParty(
            UUID.randomUUID(),
            name,
            leader,
            hashSetOf(leader)
        )
        plugin.launch {
            upsert(party)
        }
        return party
    }

    override suspend fun read(key: UUID): MutableParty? {
        withContext(Dispatchers.IO) {
            val hashKey = hashKey(key)

            val pipeline = redisCacheService.client.pipelined()
            val hasDataFn = pipeline.exists(hashKey)
            val nameFn = pipeline.hget(hashKey, "name")
            val leaderFn = pipeline.hget(hashKey, "leader")
            val membersFn = pipeline.smembers(memberKey(key))
            pipeline.execute()

            if(hasDataFn() == 0L) {
                // fetch from database
                val firstDbParty = EntityParty.findById(key) ?: return@withContext null
                val party = PartyDataProviderManagedParty(
                    key,
                    firstDbParty.name,
                    firstDbParty.leaderUUID,
                    firstDbParty.members.map { it.playerUUID }.toHashSet()
                )
                // save to redis cache
                upsert(party, redisOnly = true)
                return@withContext party
            } else {
                return@withContext PartyDataProviderManagedParty(
                    key,
                    nameFn(),
                    UUID.fromString(leaderFn()),
                    membersFn().map { UUID.fromString(it) }.toHashSet()
                )
            }
        }
        return null
    }

    override suspend fun upsert(value: Party) {
        upsert(value, redisOnly = false)
    }

    private suspend fun upsert(value: Party, redisOnly: Boolean = false) {
        withContext(Dispatchers.IO) {
            val hashKey = hashKey(value.uuid)
            val memberKey = memberKey(value.uuid)

            // Insert or update to redis
            val pipeline = redisCacheService.client.pipelined()
            pipeline.hset(
                hashKey,
                "name" to (value.name ?: ""),
                "leader" to value.leader.toString()
            )
            if(value.members.isNotEmpty()) {
                pipeline.sadd(
                    memberKey,
                    value.members.first().toString(),
                    *value.members.map { it.toString() }.toTypedArray()
                )
            }
            pipeline.expire(hashKey, 3600u)
            pipeline.expire(memberKey, 3600u)
            pipeline.execute()

            if(!redisOnly) {
                // update database
                transaction {
                    TableParties.upsert(TableParties.id) {
                        it[uuid] = value.uuid
                        it[name] = value.name ?: ""
                        it[leaderUUID] = value.leader
                    }

                    TablePartyMembers.deleteWhere { party eq value.uuid }
                    TablePartyMembers.batchInsert(value.members) {
                        this[TablePartyMembers.party] = value.uuid
                        this[TablePartyMembers.playerUUID] = it
                    }
                }
            }

        }
    }

    override suspend fun delete(key: UUID) {
        withContext(Dispatchers.IO) {
            redisCacheService.client.del("$KEY_PREFIX:${key}")
            redisCacheService.client.del("$KEY_PREFIX:${key}:members")
            transaction {
                TableParties.deleteWhere { uuid eq key }
                TablePartyMembers.deleteWhere { party eq key }
            }
        }
    }

    override suspend fun contains(key: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            val hashKey = hashKey(key)
            val inRedis = redisCacheService.client.exists(hashKey) == 1L
            if(inRedis) return@withContext true

            EntityParty.findById(key) != null
        }
    }

    inner class PartyDataProviderManagedParty(
        override val uuid: UUID,
        override var name: String?,
        defaultLeader: UUID,
        override val members: HashSet<UUID>,
    ): MutableParty {

        override var leader: UUID = defaultLeader
            private set

        private var disposed: Boolean = false

        override fun addMember(member: UUID) {
            requireNotDisposed()
            members.add(member)
            plugin.launch(Dispatchers.IO) {
                redisCacheService.client.sadd(memberKey(uuid), member.toString())
                TablePartyMembers.insertIgnore {
                    it[party] = uuid
                    it[playerUUID] = member
                }
            }
        }

        override fun removeMember(member: UUID) {
            requireNotDisposed()
            members.remove(member)
            plugin.launch(Dispatchers.IO) {
                redisCacheService.client.srem(memberKey(uuid), member.toString())
                TablePartyMembers.deleteWhere {
                    (party eq uuid) and (playerUUID eq member)
                }
            }
        }

        override fun switchLeader(leader: UUID) {
            requireNotDisposed()
            if(!members.contains(leader)) {
                throw IllegalArgumentException("Leader must be a member of the party.")
            }
            this.leader = leader
            plugin.launch(Dispatchers.IO) {
                redisCacheService.client.hdel(hashKey(uuid), "leader")
                TableParties.update({ TableParties.uuid eq uuid }) {
                    it[leaderUUID] = leader
                }
            }
        }

        override fun disband() {
            requireNotDisposed()
            disposed = true
            members.clear()
            plugin.launch {
                this@PartyManagerImpl.delete(uuid)
            }
        }

        private fun requireNotDisposed() {
            if (disposed) {
                throw IllegalStateException("Party is already disbanded.")
            }
        }

    }

}