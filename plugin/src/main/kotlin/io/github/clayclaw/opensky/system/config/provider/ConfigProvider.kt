package io.github.clayclaw.opensky.system.config.provider

import java.io.File

interface ConfigProvider {
    fun <T> read(file: File, type: Class<T>): T
    fun write(file: File, data: Any)
}

object DefaultConfigProvider {
    val json = JsonConfigProvider()
    val yaml = SnakeYamlConfigProvider()
}

fun decideConfigProvider(file: File): ConfigProvider = when(file.extension) {
    "json" -> DefaultConfigProvider.json
    "yml" -> DefaultConfigProvider.yaml
    else -> throw IllegalArgumentException("Unsupported file extension: ${file.extension}")
}