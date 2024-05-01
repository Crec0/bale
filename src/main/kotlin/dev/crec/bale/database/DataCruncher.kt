package dev.crec.bale.database

import com.mojang.authlib.GameProfile
import dev.crec.bale.database.schema.PlayerStats
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.jetbrains.exposed.sql.*
import java.nio.file.Path
import java.util.*
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.notExists
import kotlin.io.path.readText

object DataCruncher {
    suspend fun munch(server: MinecraftServer) {
        val statsDir = server.getWorldPath(LevelResource.PLAYER_STATS_DIR)
        if (statsDir.notExists()) return

        statsDir.listDirectoryEntries("*.json").forEach { file ->
            val profile = server.profileCache?.get(UUID.fromString(file.nameWithoutExtension))
            if (profile == null || profile.isEmpty) return@forEach

            readAndStoreStatsInDB(profile.get(), file)
        }
    }

    private suspend fun readAndStoreStatsInDB(profile: GameProfile, file: Path) {
        val json = Json.parseToJsonElement(file.readText())
        val stats = json.jsonObject["stats"]?.jsonObject ?: return

        DatabaseSingleton.query {
            stats.forEach { (key, subStats) ->
                val statCategory = key.removePrefix("minecraft:")

                subStats.jsonObject.forEach { sName, sValue ->
                    val strippedStat = sName.removePrefix("minecraft:")
                    val statName = "$statCategory:$strippedStat"
                    val statValue = sValue.jsonPrimitive.int

                    val selectResult = PlayerStats.selectAll()
                        .where { PlayerStats.uuid eq profile.id and (PlayerStats.stat eq statName) }
                        .firstOrNull()

                    if (selectResult == null) {
                        PlayerStats.insert {
                            it[uuid] = profile.id
                            it[name] = profile.name
                            it[stat] = statName
                            it[value] = statValue
                        }
                    } else {
                        PlayerStats.update({ PlayerStats.uuid eq profile.id and (PlayerStats.stat eq statName) }) {
                            it[name] = profile.name
                            it[value] = statValue
                        }
                    }
                }
            }
        }
    }
}
