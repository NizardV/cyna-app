package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

// POST /auth/forgot-password
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

// POST /auth/reset-password
@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

// POST /auth/confirm-email
@Serializable
data class ConfirmEmailRequest(
    val email: String,
    val code: String
)