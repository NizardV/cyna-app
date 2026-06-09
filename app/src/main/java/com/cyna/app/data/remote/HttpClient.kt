package com.cyna.app.data.remote

import com.cyna.app.data.dto.ErrorResponse
import com.cyna.app.data.util.VibrationHelper
import dev.kindling.core.components.KToastManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create(),
    vibrationHelper: VibrationHelper? = null   // null = mode preview/test sans contexte Android
): HttpClient = HttpClient(engine) {

    defaultRequest { url(baseUrl) }

    install(ContentNegotiation) { json() }

    install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        socketTimeoutMillis  = 15_000
        requestTimeoutMillis = 15_000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) { println("HTTP Client: $message") }
        }
        level = LogLevel.ALL
    }

    install(HttpCallValidator) {

        validateResponse { response ->
            val status = response.status.value
            when {
                status == 200 -> Unit

                // 4xx — erreur client : vibration warning (impulsion modérée)
                status in 400..499 -> {
                    val msg = runCatching { response.body<ErrorResponse>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    vibrationHelper?.warning()
                    KToastManager.warning("Client error ($status)", msg)
                    throw HttpException.ClientError(status, msg)
                }

                // 5xx — erreur serveur : vibration error (double impulsion forte)
                status in 500..599 -> {
                    val msg = runCatching { response.body<ErrorResponse>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    vibrationHelper?.error()
                    KToastManager.error("Server error ($status)", msg)
                    throw HttpException.ServerError(status, msg)
                }
            }
        }

        // Erreur réseau (timeout, DNS, connexion refusée) : vibration error
        handleResponseExceptionWithRequest { exception, _ ->
            if (exception is HttpException) return@handleResponseExceptionWithRequest
            vibrationHelper?.error()
            KToastManager.error("Network error", exception.message ?: "No details provided")
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

fun HttpResponse.accept(vararg codes: HttpStatusCode) = apply {
    if (status !in codes) {
        val message     = "Unexpected status: HTTP $status"
        val description = "Expected: ${codes.joinToString()}"
        KToastManager.warning(message, description)
        throw HttpException.NotAccepted("$message. $description")
    }
}

// ── Exceptions ────────────────────────────────────────────────────────────────

sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted(message: String)                         : HttpException(message)
    class ClientError(val statusCode: Int, message: String)    : HttpException(message)
    class ServerError(val statusCode: Int, message: String)    : HttpException(message)
}