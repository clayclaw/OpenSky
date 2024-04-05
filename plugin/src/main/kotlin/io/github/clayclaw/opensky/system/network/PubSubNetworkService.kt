package io.github.clayclaw.opensky.system.network

import kotlin.reflect.KClass

@JvmInline
value class PubSubChannel(val id: String)

@JvmInline
value class PubSubIdentity(val id: String)

interface PubSubSubscription {
    fun cancel()
}

interface PubSubNetworkService {

    fun init()
    fun close()

    suspend fun <T: Any> publish(channel: PubSubChannel, message: T)
    fun <T: Any> subscribe(channel: PubSubChannel, decoderClass: KClass<T>, handler: suspend (T) -> Unit): PubSubSubscription

}

fun <T: Any> KClass<T>.getPubSubChannel(): PubSubChannel {
    val classQualifiedName = this.qualifiedName ?: throw IllegalArgumentException("Class name not found: $this")
    return PubSubChannel("opensky_${classQualifiedName}")
}

suspend fun <T: Any> PubSubNetworkService.publish(message: T) {
    publish(message::class.getPubSubChannel(), message)
}

inline fun <reified T: Any> PubSubNetworkService.subscribe(noinline handler: suspend (T) -> Unit): PubSubSubscription {
    val channel = T::class.getPubSubChannel()
    return subscribe(channel, T::class, handler)
}