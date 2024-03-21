package io.github.clayclaw.opensky.cache

import io.github.clayclaw.opensky.config.ConfigCache
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import java.util.logging.Logger

@Single
class RedisCacheService(
    private val logger: Logger,
    private val configCache: ConfigCache,
): CacheService {

    private lateinit var client: KredsClient

    override fun connect() {
        runBlocking {
            client = newClient(Endpoint.from(configCache.endpoint))
            if(!configCache.password.isNullOrEmpty()) {
                var errorMessage: String? = null

                errorMessage = if(configCache.username.isNullOrEmpty()) {
                    client.auth(configCache.password!!)
                } else {
                    client.auth(configCache.username!!, configCache.password!!)
                }

                if(errorMessage.isNotEmpty()) {
                    throw IllegalStateException("Failed to authenticate with Redis: $errorMessage")
                }
            }

            // test redis connection
            client.set("opensky:test", "Hello world from OpenSky!")
            val test = client.get("opensky:test")
            client.del("opensky:test")
            logger.info("Redis test: $test")
        }
        logger.info("Redis client is connected to ${configCache.endpoint}")
    }

    override fun disconnect() {
        if(::client.isInitialized) {
            client.close()
            logger.info("Redis client is disconnected.")
        } else {
            logger.warning("Redis client is not initialized, thus it's not disconnected.")
        }
    }

}