package dev.crec.mcserverapi.server

import dev.crec.mcserverapi.connection.ClientWhitelist
import dev.crec.mcserverapi.connection.ClientWhitelistImpl
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Crypt
import java.nio.file.Path
import java.security.KeyPair

val CONFIG_PATH: Path = FabricLoader.getInstance().configDir.resolve("mc-server-api.json")

sealed interface Server {
    val whitelist: ClientWhitelist
    val keyPair: KeyPair
}

class ServerImpl : Server {
    override val whitelist: ClientWhitelist
    override val keyPair: KeyPair

    init {
        whitelist = ClientWhitelistImpl()
        whitelist.load()

        keyPair = Crypt.generateKeyPair()
    }
}
