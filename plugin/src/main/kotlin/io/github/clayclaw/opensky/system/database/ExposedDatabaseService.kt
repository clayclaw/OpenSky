package io.github.clayclaw.opensky.system.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.clayclaw.opensky.config.BaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import java.util.logging.Logger

@Single
class ExposedDatabaseService(
    private val baseConfig: BaseConfig,
    private val logger: Logger,
): DatabaseService {
    
    private lateinit var database: Database

    override fun connect() {
        runCatching {
            database = Database.connect({
                val config = HikariConfig().apply {
                    jdbcUrl = baseConfig.database.jdbcUrl
                    driverClassName = baseConfig.database.driver
                    username = baseConfig.database.username
                    password = baseConfig.database.password
                    baseConfig.database.properties.forEach { (key, value) -> addDataSourceProperty(key, value) }
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

    override fun disconnect() {
        if(::database.isInitialized) {
            TransactionManager.closeAndUnregister(database)
        } else {
            logger.warning("Database is not initialized, thus it's not closed.")
        }
    }

}