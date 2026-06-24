package com.cyna.app.domain.model

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

data class ConfirmEmailRequest(
    val email: String,
    val code: String
)