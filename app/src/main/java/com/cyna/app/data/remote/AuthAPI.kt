package com.cyna.app.data.remote

import com.cyna.app.data.dto.ConfirmEmailRequest
import com.cyna.app.data.dto.ForgotPasswordRequest
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.RefreshTokenRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.dto.ResetPasswordRequest
import com.cyna.app.data.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Passerelle vers les endpoints d'authentification de l'API Cyna (`/auth/`).
 *
 * L'authentification est basée sur des cookies HTTP (`cyna_token`, `cyna_refresh_token`)
 * renvoyés par l'API via `Set-Cookie`. Les corps de réponse de login/register ne contiennent
 * qu'un [MessageResponse] — les tokens sont injectés automatiquement dans le stockage de
 * cookies par [SessionManagerCookieStorage].
 */
internal class AuthAPI(private val client: HttpClient) {

    /** Authentifie l'utilisateur. Les cookies de session sont définis par la réponse `Set-Cookie`. */
    suspend fun login(request: LoginRequest): MessageResponse =
        client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()

    /** Crée un nouveau compte. */
    suspend fun register(request: RegisterRequest): MessageResponse =
        client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()

    /** Invalide la session côté serveur. */
    suspend fun logout(refreshToken: String) {
        client.post("auth/logout") {
            contentType(ContentType.Application.Json)
            setBodyJson(RefreshTokenRequest(refreshToken))
        }
    }

    /** Récupère le profil de l'utilisateur connecté via le cookie de session. */
    suspend fun getCurrentUser(): UserDto =
        client.get("auth/me").body()

    /** Envoie un code OTP de réinitialisation de mot de passe. */
    suspend fun forgotPassword(email: String): MessageResponse =
        client.post("auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBodyJson(ForgotPasswordRequest(email))
        }.body()

    /** Réinitialise le mot de passe avec le code OTP reçu par email. */
    suspend fun resetPassword(email: String, code: String, newPassword: String): MessageResponse =
        client.post("auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBodyJson(ResetPasswordRequest(email, code, newPassword))
        }.body()

    /** Confirme l'adresse email via le code OTP. */
    suspend fun confirmEmail(email: String, code: String): MessageResponse =
        client.post("auth/confirm-email") {
            contentType(ContentType.Application.Json)
            setBodyJson(ConfirmEmailRequest(email, code))
        }.body()
}