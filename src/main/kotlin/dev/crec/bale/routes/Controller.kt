package dev.crec.bale.routes

import dev.crec.bale.components.calculateMSPT
import dev.crec.bale.components.calculateTPS
import dev.crec.bale.components.msptCard
import dev.crec.bale.database.schema.PlayerStats
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.minecraft.server.MinecraftServer
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
    }
}


