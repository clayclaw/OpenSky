package io.github.clayclaw.opensky.island.template

import org.bukkit.World
import java.io.File

class SchematicIslandTemplate(
    private val schematicFile: File,
): IslandTemplate {

    constructor(schematicPath: String): this(File(schematicPath))

    override suspend fun createNewWorld(): World {
        if(!schematicFile.exists()) {
            throw IllegalArgumentException("Schematic file does not exist: ${schematicFile.absolutePath}")
        }
        TODO()
    }

}