package dev.crec.bale.routes

import dev.crec.bale.scraps.path
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*

suspend fun ApplicationCall.respondFullPage(
    localStyle: String? = null,
    contentBlock: suspend MAIN.() -> Unit
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
            link(href = "/static/stylesheet.css", rel = "stylesheet") { }
            script(src = "/static/htmx-cdn.js") { }
            script(src = "/static/htmx-json-encode-cdn.js") { }
            script(src = "/static/htmx-preload-cdn.js") {}
        }
        body(classes = "font-sans antialiased min-h-screen") {
            attributes["hx-ext"] = "json-enc"
            div(classes = "relative flex min-h-screen flex-col") {
                div(classes = "sticky top-0 z-50 w-full border-b border-border/40 bg-background/95 backdrop-blur text-lg mb-10") {
                    div(classes = "container flex h-14 max-w-screen-2xl items-center") {
                        a(href = "/", classes = "flex items-center space-x-2 px-4") {
                            svg {
                                attributes["width"] = "24"
                                attributes["height"] = "24"
                                attributes["viewBox"] = "0 0 24 24"
                                attributes["fill"] = "none"
                                attributes["stroke"] = "currentColor"
                                attributes["stroke-width"] = "2"
                                attributes["stroke-linecap"] = "round"
                                attributes["stroke-linejoin"] = "round"
                                path {
                                    attributes["d"] = "M2 22 16 8"
                                }
                                path {
                                    attributes["d"] =
                                        "M3.47 12.53 5 11l1.53 1.53a3.5 3.5 0 0 1 0 4.94L5 19l-1.53-1.53a3.5 3.5 0 0 1 0-4.94Z"
                                }
                                path {
                                    attributes["d"] =
                                        "M7.47 8.53 9 7l1.53 1.53a3.5 3.5 0 0 1 0 4.94L9 15l-1.53-1.53a3.5 3.5 0 0 1 0-4.94Z"
                                }
                                path {
                                    attributes["d"] =
                                        "M11.47 4.53 13 3l1.53 1.53a3.5 3.5 0 0 1 0 4.94L13 11l-1.53-1.53a3.5 3.5 0 0 1 0-4.94Z"
                                }
                                path {
                                    attributes["d"] = "M20 2h2v2a4 4 0 0 1-4 4h-2V6a4 4 0 0 1 4-4Z"
                                }
                                path {
                                    attributes["d"] =
                                        "M11.47 17.47 13 19l-1.53 1.53a3.5 3.5 0 0 1-4.94 0L5 19l1.53-1.53a3.5 3.5 0 0 1 4.94 0Z"
                                }
                                path {
                                    attributes["d"] =
                                        "M15.47 13.47 17 15l-1.53 1.53a3.5 3.5 0 0 1-4.94 0L9 15l1.53-1.53a3.5 3.5 0 0 1 4.94 0Z"
                                }
                                path {
                                    attributes["d"] =
                                        "M19.47 9.47 21 11l-1.53 1.53a3.5 3.5 0 0 1-4.94 0L13 11l1.53-1.53a3.5 3.5 0 0 1 4.94 0Z"
                                }
                            }
                            span(classes = "text-3xl font-bold ml-4") {
                                +"Bale"
                            }
                        }
                    }
                }
                main(classes = "flex mx-auto items-center relative flex-col max-w-[80%]") {
                    id = "mainContent"
                    runBlocking { contentBlock() }
                }
            }
        }
    }
}
