package io.github.clayclaw.opensky.system.config.provider

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import java.io.File

class JsonConfigProvider(
    private val providedTypeAdapters: Iterable<GsonTypeAdapterComponent<*>> = emptyList(),
    private val gsonBuilder: GsonBuilder = GsonBuilder().setPrettyPrinting()
): ConfigProvider {

    private val gson = gsonBuilder
        .apply {
            providedTypeAdapters.forEach { registerTypeAdapter(it.type, it.adapter) }
        }
        .create()

    override fun write(file: File, data: Any) {
        file.writeText(gson.toJson(data))
    }
    override fun <T> read(file: File, type: Class<T>): T {
        return file.inputStream().use {
            gson.fromJson(it.reader(), type)
        }
    }

}

interface GsonTypeAdapterComponent<T> {
    val type: Class<T>
    val adapter: TypeAdapter<T>
}