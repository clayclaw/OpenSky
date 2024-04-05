package io.github.clayclaw.opensky.system.cache

import io.github.clayclaw.opensky.config.BaseConfig
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import java.util.logging.Logger

@Single
class RedisCacheService(
    private val logger: Logger,
    private val baseConfig: BaseConfig,
): CacheService {

    internal lateinit var client: KredsClient

    override fun connect() {
        runBlocking {
            client = newClient(Endpoint.from(baseConfig.cache.endpoint))
            if(!baseConfig.cache.password.isNullOrEmpty()) {
                var errorMessage: String? = null

                errorMessage = if(baseConfig.cache.username.isNullOrEmpty()) {
                    client.auth(baseConfig.cache.password!!)
                } else {
                    client.auth(baseConfig.cache.username!!, baseConfig.cache.password!!)
                }

                if(errorMessage.isNotEmpty()) {
                    throw IllegalStateException("Failed to authenticate with Redis: $errorMessage")
                }
            }

            // test redis connection
            val test = client.ping("Hello world from open sky")
            logger.info("Redis test: $test")
        }
        logger.info("Redis client is connected to ${baseConfig.cache.endpoint}")
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