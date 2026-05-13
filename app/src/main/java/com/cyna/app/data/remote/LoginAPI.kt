package com.cyna.app.data.remote

import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

internal class LoginAPI(private val client: HttpClient) {

    /**
     * Creates a new user login with the provided username.
     *
     * @param username The username to create login for.
     * @return [LoginResponseDto] with user creation details.
     * @throws HttpException if the request fails or if the status code is not [HttpStatusCode.OK].
     */
    suspend fun createLogin(username: String): LoginResponseDto = client.createLogin(username)
}
private suspend fun HttpClient.createLogin(username: String): LoginResponseDto =
    post("login/create") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(username))
    }
        .accept(HttpStatusCode.OK, HttpStatusCode.Created)
        .body()