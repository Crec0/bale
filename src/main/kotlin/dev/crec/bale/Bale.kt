package dev.crec.bale

import dev.crec.bale.commands.Generate
import dev.crec.bale.database.DatabaseSingleton
import dev.crec.bale.database.munchers.munchPlayerStats
import dev.crec.bale.database.munchers.munchStatsRegistry
import dev.crec.bale.routes.setupRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.jetbrains.annotations.BlockingExecutor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists

const val SERVER_NAME = "bale"

val configFile: Path = FabricLoader.getInstance().configDir.resolve("bale").resolve("key.txt")
val log: Logger = LoggerFactory.getLogger(SERVER_NAME)

val dispatcher: @BlockingExecutor CoroutineDispatcher =
    Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

lateinit var engine: CIOApplicationEngine

val thread = thread(start = false, name = "bale-network-thread") {
    engine = embeddedServer(CIO, port = 8080, host = "0.0.0.0", watchPaths = listOf("classes")) {
        DatabaseSingleton.init()

        val serverRef = AtomicReference<MinecraftServer>()
        ServerLifecycleEvents.SERVER_STARTED.register { s ->
            serverRef.set(s)
            launch {
                munchStatsRegistry()
                munchPlayerStats(s)
            }
        }

        setupRouting(serverRef)
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }
    }
    engine.start(wait = true)
}

@Suppress("unused")
fun init() {
    log.info("Initializing $SERVER_NAME")

    if (configFile.notExists()) {
        configFile.parent.createDirectories()
        configFile.createFile()
    }

    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        Generate.register(dispatcher)
    }

    ServerLifecycleEvents.SERVER_STARTING.register {
        thread.start()
    }

    ServerLifecycleEvents.SERVER_STOPPING.register {
        log.info("Shutting down bale server")
        engine.stop(2000, 2000)
    }
}
