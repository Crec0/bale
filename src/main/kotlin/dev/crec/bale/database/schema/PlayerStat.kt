package dev.crec.bale.database.schema

import org.jetbrains.exposed.sql.Table

data class PlayerStat(
    val uuid: String,
    val name: String,
    val stat: String,
    val value: Int,
)

object PlayerStats : Table() {
    val uuid = uuid("uuid")
    val name = varchar("name", 64)
    val stat = varchar("stat", 256)
    val value = integer("value")
}