package dev.crec.bale.database

import dev.crec.bale.database.schema.PlayerStats
import kotlinx.coroutines.Dispatchers
import net.fabricmc.loader.api.FabricLoader
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

object DatabaseSingleton {
    fun init() {
        val driver = "org.sqlite.JDBC"
        val url = "jdbc:sqlite:${FabricLoader.getInstance().configDir.resolve("bale")}/bale.db"
        val database = Database.connect(url, driver)
        transaction(database) {
            SchemaUtils.create(PlayerStats)
        }
    }

    suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) {
        block()
    }
}
