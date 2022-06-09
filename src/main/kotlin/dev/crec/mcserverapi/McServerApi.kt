package dev.crec.mcserverapi

import dev.crec.mcserverapi.commands.ServerApiCommand
import dev.crec.mcserverapi.connection.configureAuth
import dev.crec.mcserverapi.server.Server
import dev.crec.mcserverapi.server.ServerImpl
import dev.crec.mcserverapi.websocket.configureSockets
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOG: Logger = LoggerFactory.getLogger(SERVER_NAME)
val SERVER: Server by lazy { ServerImpl() }

private val nettyEngine = embeddedServer(Netty, port = PORT) {
    configureAuth()
    configureSockets()
}

class McServerApi : ModInitializer {
    override fun onInitialize() {
        LOG.info("Initializing $SERVER_NAME")

        CommandRegistrationCallback.EVENT.register { dispatcher, isDedicated ->
            if (!isDedicated) return@register
            ServerApiCommand(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STARTED.register {
            LOG.info("Launching $SERVER_NAME on port $PORT")
            nettyEngine.start()
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            LOG.info("Shutting down $SERVER_NAME")
            nettyEngine.stop()
        }
    }
}
