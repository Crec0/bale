package dev.crec.mcserverapi.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.crec.mcserverapi.CONFIG_FILE_NAME
import dev.crec.mcserverapi.ISSUER
import dev.crec.mcserverapi.config.Client
import dev.crec.mcserverapi.config.Config
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Crypt
import java.nio.file.Path
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

val CONFIG_PATH: Path = FabricLoader.getInstance().configDir.resolve(CONFIG_FILE_NAME)

sealed interface Server {
    val config: Config
    val keyPair: KeyPair

    fun generateToken(client: Client): String
}

class ServerImpl : Server {
    override val config: Config = Config().load()
    override val keyPair: KeyPair = Crypt.generateKeyPair()

    override fun generateToken(client: Client): String {
        check(client.name.isNotEmpty()) { "Client name cannot be empty." }
        check(client.secret.isNotEmpty()) { "Client secret cannot be empty." }

        check(client == config.client) {
            "Client credentials do not match the registered client. Please check your credentials."
        }

        config.client = client

        return JWT.create()
            .withIssuer(ISSUER)
            .withClaim("name", client.name)
            .withClaim("secret-digest", client.generateSecretDigest())
            .sign(Algorithm.RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey))
    }
}
