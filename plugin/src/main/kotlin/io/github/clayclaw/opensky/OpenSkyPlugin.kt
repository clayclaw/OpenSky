package io.github.clayclaw.opensky

import io.github.clayclaw.opensky.challenge.BukkitPlayerChallengeManager
import io.github.clayclaw.opensky.command.OpenSkyBukkitCommand
import io.github.clayclaw.opensky.compatibility.pluginCompatibilityModule
import io.github.clayclaw.opensky.config.configModule
import io.github.clayclaw.opensky.data.exposed.ModelOperations
import io.github.clayclaw.opensky.island.RemoteIslandManager
import io.github.clayclaw.opensky.system.cache.CacheService
import io.github.clayclaw.opensky.system.database.DatabaseService
import io.github.clayclaw.opensky.system.network.PubSubNetworkService
import io.github.clayclaw.opensky.system.serializer.GsonSerializers
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
            single { GsonSerializers.gson }
        }
        koinApplication = startKoin {
            logger(OpenSkyPluginLogger(this@OpenSkyPlugin.logger))
            modules(systemModule, configModule, pluginCompatibilityModule, OpenSkyModule().module)
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

    private val exposedDatabaseService: DatabaseService by inject()
    private val cacheService: CacheService by inject()
    private val pubSubService: PubSubNetworkService by inject()
    private val remoteIslandManager: RemoteIslandManager by inject()
    private val bukkitPlayerChallengeManager: BukkitPlayerChallengeManager by inject()

    fun init() {
        exposedDatabaseService.connect()
        ModelOperations.createSchemasIfNotExists()

        cacheService.connect()
        pubSubService.init()
        remoteIslandManager.init()
        bukkitPlayerChallengeManager.init()
    }

    fun close() {
        bukkitPlayerChallengeManager.close()
        remoteIslandManager.close()
        pubSubService.close()
        cacheService.disconnect()
        exposedDatabaseService.disconnect()
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