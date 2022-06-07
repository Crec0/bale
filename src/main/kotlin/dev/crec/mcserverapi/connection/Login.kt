package dev.crec.mcserverapi.connection

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.RSA256
import dev.crec.mcserverapi.apiServer
import io.ktor.http.HttpStatusCode
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
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

fun Application.configureAuth() {
    install(ContentNegotiation) {
        json()
    }

    val issuer = "MCServerAPI"

    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "dev.crec.mcserverapi"
            verifier(jwkProvider, issuer) {
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
            val client = call.receive<Client>()
            val keyPair = apiServer.keyPair
            val token = JWT.create()
                .withIssuer(issuer)
                .withClaim("username", client.clientName)
                .sign(RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey))
            call.respond(hashMapOf("token" to token))
        }
    }
}
