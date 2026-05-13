package com.cyna.app.data.repository

import com.cyna.app.data.remote.LoginAPI
import com.cyna.app.domain.model.LoginResponse
import com.cyna.app.domain.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.text.equals

/**
 * Implementation of [LoginRepository] that provides mock authentication functionality.
 *
 * This repository simulates login operations with predefined responses for specific identifiers
 * and generates mock user IDs for other cases. Useful for testing and development purposes.
 *
 * @constructor Creates a new instance of the login repository implementation.
 */
internal class LoginRepositoryImpl(
    private val loginAPI: LoginAPI
) : LoginRepository {

    /**
     * Attempts to authenticate a user with the provided identification.
     *
     * This method simulates an API call with a 1-second delay and returns mock responses
     * based on the identification string:
     * - "admin" (case-insensitive): Returns successful login with userId "admin-123"
     * - "user" (case-insensitive): Returns successful login with userId "user-456"
     * - "error" (case-insensitive): Returns failed login with "Invalid credentials" error
     * - Any other identifier: Returns successful login with generated userId based on hash code
     *
     * @param identification The user's identification string (username, email, etc.)
     * @return [LoginResponse] containing authentication result with success status,
     *         user ID for successful logins, or error message for failed attempts
     *
     * @sample
     * // Successful login for admin
     * val response = loginRepository.login("admin")
     * // response.success == true, response.userId == "admin-123"
     *
     * @sample
     * // Failed login simulation
     * val response = loginRepository.login("error")
     * // response.success == false, response.error == "Invalid credentials"
     */
    override suspend fun login(identification: String): LoginResponse {
        // Simulate API call delay
        delay(1000)

        // Mock authentication logic
        return when {
            identification.equals("admin", ignoreCase = true) -> {
                LoginResponse(
                    success = true,
                    userId = "admin-123"
                )
            }
            identification.equals("user", ignoreCase = true) -> {
                LoginResponse(
                    success = true,
                    userId = "user-456"
                )
            }
            identification.equals("error", ignoreCase = true) -> {
                LoginResponse(
                    success = false,
                    error = "Invalid credentials"
                )
            }
            else -> {
                // For any other identifier, simulate successful login
                LoginResponse(
                    success = true,
                    userId = "user-${identification.hashCode().absoluteValue}"
                )
            }
        }
    }
}