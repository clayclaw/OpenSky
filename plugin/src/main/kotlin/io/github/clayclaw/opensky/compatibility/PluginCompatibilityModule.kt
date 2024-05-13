package io.github.clayclaw.opensky.compatibility

import io.github.clayclaw.opensky.compatibility.impl.PlaceholderAPIFacade
import io.github.clayclaw.opensky.compatibility.impl.VaultEconomyFacade
import org.bukkit.Bukkit
import org.koin.dsl.module
import java.lang.IllegalStateException

private fun isPluginEnabled(pluginName: String) = Bukkit.getPluginManager().isPluginEnabled(pluginName)

val pluginCompatibilityModule = module {
    single<EconomyFacade> {
        if(isPluginEnabled("Vault")) VaultEconomyFacade()
        throw IllegalStateException("No economy plugin support enabled: Vault")
    }
    single<PlaceholderFacade> {
        if(isPluginEnabled("PlaceholderAPI")) PlaceholderAPIFacade(get()).also { it.init() }
        throw IllegalStateException("No placeholder plugin support enabled: PlaceholderAPI")
    }
}