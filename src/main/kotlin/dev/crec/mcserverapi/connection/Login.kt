package dev.crec.mcserverapi.connection

import com.auth0.jwk.JwkProviderBuilder
import dev.crec.mcserverapi.ISSUER
import dev.crec.mcserverapi.SERVER
import dev.crec.mcserverapi.config.Client
import io.ktor.http.HttpStatusCode
import io.ktor.http.IllegalHeaderNameException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.SerializationException
import java.util.concurrent.TimeUnit

fun Application.configureAuth() {
    install(ContentNegotiation) {
        json()
    }

    val jwkProvider =
        JwkProviderBuilder(ISSUER).cached(10, 24, TimeUnit.HOURS).rateLimited(10, 1, TimeUnit.MINUTES).build()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "dev.crec.mcserverapi"
            verifier(jwkProvider, ISSUER) {
                acceptLeeway(3)
            }
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
    routing {
        post("/login") {
            try {
                val client = call.receive<Client>()
                val token = SERVER.generateToken(client)
                call.respond(mapOf("token" to token))
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request. Please check your request body.")
            } catch (e: Exception) {
                call.respond(
                    if (e is IllegalHeaderNameException)
                        HttpStatusCode.Unauthorized
                    else
                        HttpStatusCode.InternalServerError,
                    e.localizedMessage
                )
            }
        }
    }
}
