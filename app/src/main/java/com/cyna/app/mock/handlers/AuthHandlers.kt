package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val authHandlers: List<MockHandler> = listOf(

    // POST /auth/login → { message }  (real API uses Set-Cookie for tokens)
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/login",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val email = json?.get("email")?.jsonPrimitive?.content ?: ""
            if (email == "error@example.com") error("Identifiants invalides.")
            MessageResponse("Connexion réussie.")
        }
    ),

    // POST /auth/register → { message }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/register",
        resolver = { _, _ -> MessageResponse("Inscription réussie.") }
    ),

    // POST /auth/refresh → { message }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/refresh",
        resolver = { _, _ -> MessageResponse("Rafraîchissement réussi.") }
    ),

    // POST /auth/logout
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/logout",
        resolver = { _, _ ->
            if (Math.random() < 0.25) error("Erreur lors de la déconnexion côté serveur.")
            MessageResponse("Déconnecté avec succès.")
        }
    ),

    // GET /auth/me → UserDto
    MockHandler(
        method = HttpMethod.Get,
        path = "/auth/me",
        resolver = { _, _ -> MockFactories.makeDemoUser() }
    ),

    // POST /auth/forgot-password → always 200 (anti-enumeration)
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/forgot-password",
        resolver = { _, _ ->
            // Simulate small delay for realism; always 200
            MessageResponse("Si cet email est enregistré, un code de réinitialisation a été envoyé.")
        }
    ),

    // POST /auth/reset-password → { message } or error for wrong code
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/reset-password",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val code = json?.get("code")?.jsonPrimitive?.content ?: ""
            // Simulate: code "000000" always fails, anything else succeeds
            if (code == "000000") error("Code invalide ou expiré.")
            MessageResponse("Mot de passe réinitialisé avec succès.")
        }
    ),

    // POST /auth/confirm-email → { message } or error for wrong code
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/confirm-email",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val code = json?.get("code")?.jsonPrimitive?.content ?: ""
            // Simulate: code "000000" always fails, anything else succeeds
            if (code == "000000") error("Code invalide ou expiré.")
            MessageResponse("Adresse email confirmée avec succès.")
        }
    ),
)