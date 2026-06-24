package com.cyna.app.data.remote

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.TwoFactorSetupDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class TotpConfirmRequest(val totpCode: String)

internal class TwoFactorAPI(private val client: HttpClient) {

    /** POST /auth/2fa/setup → { secret, otpAuthUrl } */
    suspend fun setup(): TwoFactorSetupDto =
        client.post("auth/2fa/setup") {
            contentType(ContentType.Application.Json)
        }.body()

    /** POST /auth/2fa/confirm → { message } */
    suspend fun confirm(totpCode: String): MessageResponse =
        client.post("auth/2fa/confirm") {
            contentType(ContentType.Application.Json)
            setBodyJson(TotpConfirmRequest(totpCode))
        }.body()
}