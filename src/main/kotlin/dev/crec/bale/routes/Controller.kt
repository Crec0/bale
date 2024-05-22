package dev.crec.bale.routes

import dev.crec.bale.components.calculateMSPT
import dev.crec.bale.components.calculateTPS
import dev.crec.bale.components.msptCard
import dev.crec.bale.components.playerStats
import dev.crec.bale.database.DatabaseSingleton.query
import dev.crec.bale.database.schema.DefaultStats
import dev.crec.bale.database.schema.PlayerStats
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.dataList
import kotlinx.html.id
import kotlinx.html.option
import kotlinx.html.stream.appendHTML
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.MinecraftServer
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import java.util.concurrent.atomic.AtomicReference

fun Application.setupRouting(serverRef: AtomicReference<MinecraftServer>) {
    routing {
        staticResources("/static", "static")
        get("/") {
            call.respondFullPage {
                msptCard()
            }
        }
        get("/stats/mspt") {
            call.respondText { "%.2f".format(calculateMSPT(serverRef)) }
        }
        get("/stats/tps") {
            call.respondText { "%.2f".format(calculateTPS(serverRef)) }
        }
        get("/stats") {
            call.respondFullPage {
                playerStats()
            }
        }
        get("/stats/player/{stat}") {
            val stat = call.parameters["stat"] ?: return@get call.respondText("Invalid stat")
            val result = query {
                PlayerStats.select(PlayerStats.name, PlayerStats.value.sum()).where { PlayerStats.stat eq stat }
            }
            call.respondText { result.toString() }
        }
        post("/stats/search") {
            val search = call.receiveNullable<Search>() ?: Search()
            val result = query {
                DefaultStats.selectAll()
                    .where { DefaultStats.stat like "%${search.keyword}%" }
                    .limit(10)
                    .map { search.prefixed(it[DefaultStats.stat]) }
            }
            dev.crec.bale.log.info("search: $search, result: $result")
            call.respondText {
                buildString {
                    appendHTML().dataList {
                        id = "stats-suggestions"
                        result.forEach {
                            option {
                                +it
                            }
                        }
                    }
                }
            }
        }
        put("/stats/player/{stat}/create") {
            val stat = call.parameters["stat"] ?: return@put call.respondText("Invalid stat")

            val count = query {
                PlayerStats.select(PlayerStats.name).where { PlayerStats.stat eq stat }.count()
            }

            if (count > 0) {
                call.respondText("Stat already exists", status = HttpStatusCode.Conflict)
                return@put
            }

            println(call.receiveText())

            call.respondText("created")
        }
    }
}

@Serializable
data class Search(@SerialName("search") val query: String = "") {
    private val terms = run {
        val tokens = mutableListOf<String>()
        val builder = StringBuilder()

        for (char in query) {
            when {
                char.isWhitespace() || char == '-' || char == '+' || char == '*' || char == '/' -> {
                    tokens.add(builder.toString())
                    if (!char.isWhitespace()) {
                        tokens.add(char.toString())
                    }
                    builder.clear()
                }
                else -> {
                    builder.append(char)
                }
            }
        }
        tokens.add(builder.toString())
        builder.clear()

        tokens.map { it.trim() }.filter { it.isNotEmpty() }
    }

    val keyword = if (terms.isNotEmpty()) terms.last() else ""

    fun prefixed(value: String): String {
        return terms.dropLast(1).joinToString(" ") + " $value"
    }
}