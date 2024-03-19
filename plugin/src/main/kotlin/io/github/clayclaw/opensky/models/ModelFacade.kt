package io.github.clayclaw.opensky.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface ModelFacade<T, KeyT> {

    suspend fun read(key: KeyT): T?
    suspend fun upsert(key: KeyT, value: T)
    suspend fun delete(key: KeyT)

    suspend fun contains(key: KeyT): Boolean

}

suspend fun <T, KeyT> ModelFacade<T, KeyT>.readOrThrow(key: KeyT): T {
    return read(key) ?: throw NoSuchElementException("No such element with key $key")
}

suspend fun <T, KeyT> ModelFacade<T, KeyT>.readOrInsert(key: KeyT, defaultValue: () -> T) = coroutineScope {
    read(key) ?: async(Dispatchers.IO) {
        val value = defaultValue()
        upsert(key, value)
    }.join()
}