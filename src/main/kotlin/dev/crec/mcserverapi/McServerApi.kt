package dev.crec.mcserverapi

import dev.crec.mcserverapi.connection.configureAuth
import dev.crec.mcserverapi.server.Server
import dev.crec.mcserverapi.server.ServerImpl
import dev.crec.mcserverapi.websocket.configureSockets
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOG: Logger = LoggerFactory.getLogger("MCServerAPI")
val apiServer: Server = ServerImpl()

class McServerApi : ModInitializer {
    override fun onInitialize() {
        LOG.info("MCServerAPI is now initializing!")
        Thread {
            embeddedServer(Netty, port = 6969) {
                configureAuth()
                configureSockets()
            }.start(wait = true)
        }.start()
        LOG.info("MCServerAPI is now running!")
    }
}
