package io.github.clayclaw.opensky.system.network

import com.github.shynixn.mccoroutine.bukkit.launch
import com.google.gson.Gson
import io.github.clayclaw.opensky.OpenSkyPlugin
import io.github.clayclaw.opensky.system.cache.RedisCacheService
import io.github.clayclaw.opensky.config.ConfigCache
import io.github.clayclaw.opensky.extension.debug
import io.github.crackthecodeabhi.kreds.connection.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.util.logging.Logger
import kotlin.reflect.KClass

@Single
class RedisPubSubService(
    private val logger: Logger,
    private val configCache: ConfigCache,
    private val redisService: Lazy<RedisCacheService>,
    private val gson: Gson,
    private val plugin: OpenSkyPlugin,
): AbstractKredsSubscriber(), PubSubNetworkService {

    private lateinit var subscriberClient: KredsSubscriberClient
    private val subscriptions = HashMap<PubSubChannel, HashSet<RedisPubSubSubscription>>()

    override fun init() {
        plugin.launch {
            subscriberClient = newSubscriberClient(
                Endpoint.from(configCache.endpoint),
                this@RedisPubSubService,
            )
            testPubSubChannel()
        }
    }

    override fun close() {
        runBlocking {
            subscriberClient.unsubscribe(*subscriptions.keys.map { it.id }.toTypedArray())
            subscriberClient.close()
        }
        subscriptions.clear()
    }

    private suspend fun testPubSubChannel() {
        var subscription: PubSubSubscription? = null
        subscription = subscribe<TestMessage> {
            logger.info("Received test message: $it")
            subscription!!.cancel()
        }
        publish(TestMessage())
    }

    override suspend fun <T : Any> publish(channel: PubSubChannel, message: T) {
        val jsonString = gson.toJson(message)
        redisService.value.client.publish(channel.id, jsonString)
    }

    override suspend fun <T : Any> subscribe(
        channel: PubSubChannel,
        decoderClass: KClass<T>,
        handler: (T) -> Unit
    ): PubSubSubscription {
        val subscription = RedisPubSubSubscription(channel, decoderClass) { anyObj ->
            handler.invoke(anyObj as T)
        }
        if(!subscriptions.containsKey(channel)) {
            withContext(Dispatchers.IO) {
                subscriberClient.subscribe(channel.id)
            }
        } else {
            val firstDecoderClass = subscriptions[channel]!!.first().decoderClass
            if(firstDecoderClass != decoderClass) {
                throw IllegalArgumentException("Subscription channel $channel already subscribed with different decoder class: $firstDecoderClass")
            }
        }
        val subscriptionList = subscriptions.getOrPut(channel) { HashSet() }
        subscriptionList.add(subscription)
        return subscription
    }

    inner class RedisPubSubSubscription(
        private val channel: PubSubChannel,
        internal val decoderClass: KClass<*>,
        internal val handler: (Any) -> Unit,
    ): PubSubSubscription {

        override fun cancel() {
            subscriptions[channel]?.remove(this)
            if(subscriptions[channel]?.isEmpty() == true) {
                plugin.launch(Dispatchers.IO) {
                    subscriberClient.unsubscribe(channel.id)
                }
                subscriptions.remove(channel)
            }
        }

    }

    override fun onMessage(channel: String, message: String) {
        logger.debug("Received message: $message from redis pub/sub channel $channel")
        val channelObj = PubSubChannel(channel)
        val subscriptionList = subscriptions[channelObj] ?: return
        if(subscriptionList.isEmpty()) return
        val messageObj = gson.fromJson(message, subscriptionList.first().decoderClass.java)
        subscriptionList.forEach { it.handler.invoke(messageObj) }
    }

    override fun onSubscribe(channel: String, subscribedChannels: Long) {
        logger.info("Subscribed to redis pub/sub channel: $channel")
    }

    override fun onUnsubscribe(channel: String, subscribedChannels: Long) {
        logger.info("Unsubscribed from redis pub/sub channel: $channel")
    }

    override fun onException(ex: Throwable) {
        logger.severe("Redis PubSub channel exception:")
        ex.printStackTrace()
    }

    data class TestMessage(val message: String = "hello-redis pubsub!")
}