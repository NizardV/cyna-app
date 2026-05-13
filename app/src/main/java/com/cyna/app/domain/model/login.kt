package com.cyna.app.domain.model

data class LoginResponse(
    val success: Boolean,
    val userId: String? = null,
    val error: String? = null
)