package io.github.clayclaw.opensky.config

import io.github.clayclaw.opensky.config.comp.ConfigCache
import io.github.clayclaw.opensky.config.comp.ConfigDatabase
import io.github.clayclaw.opensky.config.comp.ConfigSystem
import io.github.clayclaw.opensky.island.loader.IslandLoaders

data class BaseConfig(
    var serverId: String = "opensky-server",
    var islandWorldLoader: String = IslandLoaders.ASWM.toString(),
    var database: ConfigDatabase = ConfigDatabase(),
    var cache: ConfigCache = ConfigCache(),
    var system: ConfigSystem = ConfigSystem(),
) {}