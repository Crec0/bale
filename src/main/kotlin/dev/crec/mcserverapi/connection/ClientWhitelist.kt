package dev.crec.mcserverapi.connection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.crec.mcserverapi.LOG
import dev.crec.mcserverapi.server.CONFIG_PATH
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.readText
import kotlin.io.path.writeText

val GSON: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

data class Client(
    val clientName: String,
    val clientDigest: String
)

sealed interface ClientWhitelist {
    val entryMap: MutableMap<String, Client>

    fun add(client: Client)
    fun remove(client: Client)
    fun contains(client: Client): Boolean
    fun get(client: Client): Client?
    fun toJson(): String
    fun fromJson(json: JsonObject)
    fun save()
    fun load()
}

data class ClientWhitelistImpl(
    override val entryMap: MutableMap<String, Client> = mutableMapOf()
) : ClientWhitelist {
    override fun add(client: Client) {
        entryMap[client.clientName] = client
    }

    override fun remove(client: Client) {
        entryMap.remove(client.clientName)
    }

    override fun contains(client: Client): Boolean {
        return entryMap.containsKey(client.clientName)
    }

    override fun get(client: Client): Client? {
        return entryMap[client.clientName]
    }

    override fun toJson(): String {
        val json = JsonObject()
        entryMap.forEach { (name, client) ->
            json.addProperty(name, client.clientDigest)
        }
        return json.toString()
    }

    override fun fromJson(json: JsonObject) {
        json.entrySet().forEach { (name, value) ->
            entryMap[name] = Client(name, value.asString)
        }
    }

    override fun save() {
        try {
            CONFIG_PATH.parent.createDirectories()
            CONFIG_PATH.createFile()
            CONFIG_PATH.writeText(toJson())
        } catch (e: Exception) {
            LOG.error("Failed to write whitelist file", e)
            LOG.error(e.stackTraceToString())
        }
    }

    override fun load() {
        try {
            val configText = CONFIG_PATH.readText()
            val json = GSON.fromJson(configText, JsonObject::class.java)
            fromJson(json)
        } catch (e: Exception) {
            LOG.error("Failed to read whitelist file", e)
            LOG.error(e.stackTraceToString())

            try {
                LOG.info("Creating new whitelist file")

                CONFIG_PATH.parent.createDirectories()
                CONFIG_PATH.createFile()
                CONFIG_PATH.writeText(toJson())

                LOG.info("Created new whitelist file")
            } catch (e: Exception) {
                LOG.error("Failed to create whitelist file", e)
                LOG.error(e.stackTraceToString())
            }
        }
    }
}
