package io.github.clayclaw.opensky.compatibility.impl

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlaceholderAPIFacade(
    private val plugin: JavaPlugin,
): PlaceholderExpansion() {

    private val placeholderMap : HashMap<String, (Player) -> String> = hashMapOf()

    override fun getIdentifier(): String {
        return "opensky"
    }

    override fun getAuthor(): String {
        return "ClayClaw"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        if(placeholderMap.containsKey(identifier)) {
            return placeholderMap[identifier]!!.invoke(player)
        }
        return null
    }

    internal fun init() {
        register()
    }

}