package io.github.clayclaw.opensky.config

import io.github.clayclaw.opensky.config.comp.ConfigCache
import io.github.clayclaw.opensky.config.comp.ConfigDatabase
import io.github.clayclaw.opensky.config.comp.ConfigSystem

data class BaseConfig(
    var serverId: String = "opensky-server",
    var database: ConfigDatabase = ConfigDatabase(),
    var cache: ConfigCache = ConfigCache(),
    var system: ConfigSystem = ConfigSystem(),
) {}