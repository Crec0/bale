package dev.crec.bale.database.munchers

import dev.crec.bale.database.DatabaseSingleton
import dev.crec.bale.database.schema.DefaultStats
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.stats.Stat
import net.minecraft.stats.StatType
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.jetbrains.exposed.sql.upsert

suspend fun munchStatsRegistry() {
    val stats = buildList {
        addAll(ObjectiveCriteria.getCustomCriteriaNames())
        BuiltInRegistries.STAT_TYPE.forEach { statType ->
            statType.registry.forEach {
                add(getName(statType, it).replace("minecraft.", ""))
            }
        }
    }

    insertOrUpdateStats(stats)
}

suspend fun insertOrUpdateStats(stats: List<String>) = DatabaseSingleton.query {
    stats.forEach { statName ->
        DefaultStats.upsert {
            it[stat] = statName
        }
    }
}

fun <T> getName(statType: StatType<T>, obj: Any): String {
    return Stat.buildName(statType, obj as T)
}
