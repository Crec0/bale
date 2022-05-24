package dev.crec.mcserverapi

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import net.fabricmc.api.ModInitializer

fun Application.module() {
    install(WebSockets)

    routing {
        webSocket("/") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val received = frame.readText()
                send(">> $received")
            }
        }
    }
}

class McServerApi : ModInitializer {
    override fun onInitialize() {
        Thread { EngineMain.main(arrayOf()) }.start()
    }
}
