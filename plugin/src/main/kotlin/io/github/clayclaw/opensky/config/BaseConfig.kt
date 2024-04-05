package io.github.clayclaw.opensky.config

import io.github.clayclaw.opensky.config.comp.ConfigCache
import io.github.clayclaw.opensky.config.comp.ConfigDatabase

data class BaseConfig(
    var serverId: String = "opensky-server",
    var database: ConfigDatabase = ConfigDatabase(),
    var cache: ConfigCache = ConfigCache(),
) {}