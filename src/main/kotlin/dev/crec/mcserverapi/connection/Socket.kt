package dev.crec.mcserverapi.websocket

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = true
    }

    routing {
        webSocket("/chat") {
            val headers = this
            val token = call.parameters["token"] ?: return@webSocket close(
                CloseReason(
                    CloseReason.Codes.VIOLATED_POLICY,
                    "No token provided"
                )
            )

            if (!isKnownTokens(token)) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                return@webSocket
            }

            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    outgoing.send(frame)
                }
            }
        }
    }
}

fun isKnownTokens(token: String): Boolean {
    return true
}
