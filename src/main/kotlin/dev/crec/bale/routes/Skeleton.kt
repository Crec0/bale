package dev.crec.bale.routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.*

suspend fun ApplicationCall.respondFullPage(
    localStyle: String? = null,
    contentBlock: MAIN.() -> Unit
) {
    this.respondHtml {
        lang = "en"
        head {
            title { +"Bale" }
            meta { charset = "UTF-8" }
            meta {
                name = "viewport"
                content = "width=device-width, initial-scale=1"
            }
            link {
                rel = "icon"
                href = "/static/favicon.ico"
                type = "image/x-icon"
                sizes = "any"
            }
            script(src = "/static/tailwind-cdn.js") { }
            script(src = "/static/htmx-cdn.js") { }
            script(src = "/static/htmx-json-encode-cdn.js") { }
            script(src = "/static/htmx-preload-cdn.js") {}
        }
        body(classes = "bg-black text-white") {
            attributes["hx-ext"] = "json-enc"

            div(classes = "w-full min-h-full p-20 items-center flex flex-col") {
                main(classes = "w-full max-w-screen-2xl flex flex-col items-center") {
                    id = "mainContent"
                    contentBlock.invoke(this)
                }
            }
        }
    }
}
