package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*

// ---------------------------------------------------------------------------
// Auth handlers — miroir de handlers/auth.js
// ---------------------------------------------------------------------------

val authHandlers: List<MockHandler> = listOf(

    // POST /auth/login
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/login",
        resolver = { _, _ -> MockFactories.makeAuthResponse(MockFactories.makeDemoUser()) }
    ),

    // POST /auth/register
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/register",
        resolver = { _, _ -> MockFactories.makeAuthResponse() }
    ),

    // POST /auth/logout — peut échouer aléatoirement (25%)
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/logout",
        resolver = { _, _ ->
            if (Math.random() < 0.25) error("Erreur lors de la déconnexion côté serveur.")
            MessageResponse("Déconnecté avec succès.")
        }
    ),

    // POST /auth/reset-password
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/reset-password",
        resolver = { _, _ -> MessageResponse("Mot de passe réinitialisé avec succès.") }
    ),
)