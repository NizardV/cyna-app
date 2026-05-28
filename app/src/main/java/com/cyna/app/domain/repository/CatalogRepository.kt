package com.cyna.app.domain.repository

import com.cyna.app.domain.model.LoginResponse
import com.cyna.app.data.remote.ChallengeAPI
import com.cyna.app.domain.model.Challenge
import com.cyna.app.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CatalogRepository {
    /**
     * Authenticates a user with the provided identification.
     *
     * @param identification The user's identification (username, email, etc.)
     * @return LoginResponse containing authentication result
     */
    suspend fun login(identification: String): LoginResponse
}