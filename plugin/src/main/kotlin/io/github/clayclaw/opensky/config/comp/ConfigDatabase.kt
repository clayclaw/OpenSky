package io.github.clayclaw.opensky.config.comp

data class ConfigDatabase(
    var jdbcUrl: String = "jdbc:mysql://localhost:3306/db?autoReconnect=true",
    var driver: String = "com.mysql.jdbc.Driver",
    var username: String = "butter",
    var password: String = "cheesecake",
    var tablePrefix: String = "opensky_",
    var properties: Map<String, String> = HashMap(),
)