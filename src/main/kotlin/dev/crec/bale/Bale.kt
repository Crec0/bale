package dev.crec.bale

import dev.crec.bale.commands.Generate
import dev.crec.bale.routes.setupRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists

const val SERVER_NAME = "bale"

val configFile: Path = Path.of("config", "bale", "key.txt")
//val configFile: Path = FabricLoader.getInstance().configDir.resolve("bale").resolve("key.txt")
val LOG: Logger = LoggerFactory.getLogger(SERVER_NAME)

lateinit var server: NettyApplicationEngine
val thread = thread(start = false, name = "bale-network-thread") {
    server = embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        setupRouting()
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }
    }
    server.start(wait = true)
}

fun main() {
    serve()
}

fun serve() {
    thread.start()
}

fun stop() {
    LOG.info("Shutting down bale server")
    server.stop(2000, 2000)
    thread.interrupt()
}

@Suppress("unused")
fun init() {
    LOG.info("Initializing $SERVER_NAME")

    if (configFile.notExists()) {
        configFile.parent.createDirectories()
        configFile.createFile()
    }

    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        Generate.register(dispatcher)
    }
}
