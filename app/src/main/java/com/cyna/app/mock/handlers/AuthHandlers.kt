package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*

// ---------------------------------------------------------------------------
// Auth handlers — mirrors handlers/auth.js
// ---------------------------------------------------------------------------

val authHandlers: List<MockHandler> = listOf(

    // POST /auth/logout
    // The client deletes its token regardless — handler may fail randomly
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

    // GET /auth/me — fixed demo user
    MockHandler(
        method = HttpMethod.Get,
        path = "/auth/me",
        resolver = { _, _ -> MockFactories.makeDemoUser() }
    ),
)
