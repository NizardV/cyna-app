package com.cyna.app.domain.repository

import com.cyna.app.domain.model.LoginResponse

interface LoginRepository {
    /**
     * Authenticates a user with the provided identification.
     *
     * @param identification The user's identification (username, email, etc.)
     * @return LoginResponse containing authentication result
     */
    suspend fun login(identification: String): LoginResponse
}