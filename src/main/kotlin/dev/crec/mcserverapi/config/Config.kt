package dev.crec.mcserverapi.config

import dev.crec.mcserverapi.LOG
import dev.crec.mcserverapi.server.CONFIG_PATH
import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Client(
    @EncodeDefault
    val name: String = "",
    @EncodeDefault
    val secret: String = "",
) {
    fun generateSecretDigest(): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(secret.toByteArray())
            .joinToString(separator = "") { "%02x".format(it) }
    }
}

private val json = Json { prettyPrint = true }

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Config(
    @EncodeDefault
    val port: String = "6969",
    @EncodeDefault
    val host: String = "0.0.0.0",
    @EncodeDefault @Contextual
    var client: Client = Client()
) {
    fun save() {
        try {
            if (!CONFIG_PATH.parent.exists())
                CONFIG_PATH.parent.createDirectories()

            if (!CONFIG_PATH.exists())
                CONFIG_PATH.createFile()
            CONFIG_PATH.writeText(json.encodeToString(this))
        } catch (e: Exception) {
            LOG.error("Failed to save config {}", e.stackTraceToString())
        }
    }

    fun load(): Config {
        try {
            if (!CONFIG_PATH.exists())
                save()

            return json.decodeFromString(CONFIG_PATH.readText())
        } catch (e: Exception) {
            LOG.error("Failed to load config. {}", e.stackTraceToString())
        }

        return Config()
    }
}
