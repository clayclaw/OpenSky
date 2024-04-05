package io.github.clayclaw.opensky.config

import io.github.clayclaw.opensky.system.config.readConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val configModule = module {
    singleConfig<BaseConfig>("config", "plugins/OpenSky/config.yml")
    singleConfig<ConfigMessage>("messages", "plugins/OpenSky/messages.yml")
}

inline fun <reified T: Any> Module.singleConfig(qualifier: String, path: String) {
    val config = readConfig<T>(path)
    single(named(qualifier)) { config }
    single { config.content }
}