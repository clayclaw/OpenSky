package io.github.clayclaw.opensky.config

data class ConfigCache(
    var endpoint: String = "localhost:6379",
    var username: String? = null,
    var password: String? = null,
)
