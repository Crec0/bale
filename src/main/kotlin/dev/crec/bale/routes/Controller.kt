package dev.crec.bale.routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.setupRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/chacha") {
            call.respondHtml {
                head {
                    title { +"Chacha" }
                }
                body {
                    h1 { +"Chacha" }
                    p { +"Chacha is a good" }
                }
            }
        }
    }
}