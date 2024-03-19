package io.github.clayclaw.opensky.system.config

import io.github.clayclaw.opensky.system.config.provider.ConfigProvider
import io.github.clayclaw.opensky.system.config.provider.decideConfigProvider
import java.io.File

class Config<T: Any>(
    private val file: File,
    private val provider: ConfigProvider,
    private val typeClass: Class<T>
) {
    lateinit var content: T

    init {
        if(!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            try {
                content = typeClass.getConstructor().newInstance()
            } catch(e: Exception) {
                throw IllegalArgumentException("Failed to create new instance of ${typeClass.canonicalName}: ", e)
            }
            save()
        } else {
            reload()
        }
    }

    fun reload() {
        content = provider.read(file, typeClass)
    }
    fun save() {
        provider.write(file, content)
    }
}


inline fun <reified T: Any> readConfig(path: String): Config<T> {
    return readConfig(File(path))
}
inline fun <reified T: Any> readConfig(path: String, provider: ConfigProvider): Config<T> {
    return readConfig(File(path), provider)
}
inline fun <reified T: Any> readConfig(file: File, provider: ConfigProvider = decideConfigProvider(file)): Config<T> {
    return Config(file, provider, T::class.java)
}

inline fun <reified T: Any> readMultiConfig(path: String): Sequence<Config<T>> {
    return readMultiConfig(File(path))
}
inline fun <reified T: Any> readMultiConfig(path: String, provider: ConfigProvider): Sequence<Config<T>> {
    return readMultiConfig(File(path), provider)
}
inline fun <reified T: Any> readMultiConfig(file: File, provider: ConfigProvider = decideConfigProvider(file)): Sequence<Config<T>> {
    return file.walkTopDown().filter { it.isFile }.map { readConfig(it, provider) }
}