package dev.crec.bale.routes

import dev.crec.bale.scraps.path
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.h1
import kotlinx.html.svg

fun Application.setupRouting() {
    routing {
        staticResources("/static", "static")
        get("/") {
            call.respondFullPage {
                h1 {
                    +"We pepepe]"
                }
                svg {
                    attributes["viewBox"] = "0 0 24 24"
                    attributes["stroke"] = "red"
                    attributes["class"] = "w-16 h-16"
                    path {
                        attributes["stroke-linecap"] = "round"
                        attributes["stroke-linejoin"] = "round"
                        attributes["stroke-width"] = "2"
                        attributes["d"] = "M12 6v6m0 0v6m0-6h6m-6 0H6"
                    }
                }
            }
        }
        get("/stats/{stat}") {
            val stat = call.parameters["stat"]
            call.respondFullPage {
                h1 {
                    +"$stat"
                }
            }
        }
    }
}