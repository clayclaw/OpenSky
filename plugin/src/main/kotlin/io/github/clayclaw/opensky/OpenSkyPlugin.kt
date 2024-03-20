package io.github.clayclaw.opensky

import io.github.clayclaw.opensky.cache.RedisCacheService
import io.github.clayclaw.opensky.command.OpenSkyBukkitCommand
import io.github.clayclaw.opensky.config.configModule
import io.github.clayclaw.opensky.database.DatabaseService
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.module
import org.koin.ksp.generated.module

class OpenSkyPlugin: JavaPlugin() {

    private lateinit var koinApplication: KoinApplication
    private lateinit var moduleBootstrap: OpenSkyModuleBootstrap

    override fun onEnable() {
        val systemModule = module {
            single { this@OpenSkyPlugin }
            single { this@OpenSkyPlugin.logger }
        }
        koinApplication = startKoin {
            logger(OpenSkyPluginLogger(this@OpenSkyPlugin.logger))
            modules(systemModule, configModule, OpenSkyModule().module)
        }
        logger.info("Modules initialized, loading components...")
        moduleBootstrap = OpenSkyModuleBootstrap()
        moduleBootstrap.init()

        this.server.getPluginCommand("opensky")?.setExecutor(OpenSkyBukkitCommand())
        logger.info("Components loaded.")
    }

    override fun onDisable() {
        if(::koinApplication.isInitialized) {
            logger.info("Closing components...")
            moduleBootstrap.close()
            logger.info("Components are closed.")
            koinApplication.close()
            logger.info("KoinApplication is closed.")
        } else {
            logger.warning("KoinApplication is not initialized, thus it's not closed.")
        }
    }

}

class OpenSkyModuleBootstrap: KoinComponent {

    private val databaseService: DatabaseService by inject()
    private val cacheService: RedisCacheService by inject()

    fun init() {
        databaseService.connect()
        cacheService.connect()
    }

    fun close() {
        databaseService.disconnect()
        cacheService.disconnect()
    }

}

class OpenSkyPluginLogger(
    private val logger: java.util.logging.Logger
): Logger() {

    override fun display(level: Level, msg: MESSAGE) {
        when(level) {
            Level.DEBUG -> logger.fine(msg)
            Level.INFO -> logger.info(msg)
            Level.WARNING -> logger.warning(msg)
            Level.ERROR -> logger.severe(msg)
            Level.NONE -> logger.finest(msg)
        }
    }

}