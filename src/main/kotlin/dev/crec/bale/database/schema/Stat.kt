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
    val stat = varchar("stat", 256).references(DefaultStats.stat)
    val value = integer("value")
}

object DefaultStats : Table() {
    val stat = varchar("stat", 64).uniqueIndex()
}

object CustomStats : Table() {
    val name = varchar("name", 64).uniqueIndex()
    val expression = varchar("expr", 256)
}