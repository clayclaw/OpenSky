package io.github.clayclaw.opensky.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.clayclaw.opensky.config.ConfigDatabase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import java.util.logging.Logger

@Single
class DatabaseService(
    private val config: ConfigDatabase,
    private val logger: Logger,
) {

    private lateinit var database: Database

    fun connect() {
        runCatching {
            database = Database.connect({
                val config = HikariConfig().apply {
                    jdbcUrl = config.jdbcUrl
                    driverClassName = config.driver
                    username = config.username
                    password = config.password
                    config.properties.forEach { (key, value) -> addDataSourceProperty(key, value) }
                }
                val dataSource = HikariDataSource(config)
                dataSource.connection
            })

            // Test Connection
            transaction {
                val conn = TransactionManager.currentOrNull()?.connection
                logger.info("Database connection test: ${conn?.prepareStatement("SELECT 1+1;", false)?.executeQuery()}")
            }

        }.onFailure {
            logger.warning("Failed to connect database! Some or all features may be malfunction! $it")
        }.onSuccess {
            logger.info("Connected to database successfully.")
        }
    }

    fun disconnect() {
        if(::database.isInitialized) {
            TransactionManager.closeAndUnregister(database)
        } else {
            logger.warning("Database is not initialized, thus it's not closed.")
        }
    }

}