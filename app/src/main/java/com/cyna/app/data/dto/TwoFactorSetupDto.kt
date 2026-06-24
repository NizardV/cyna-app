package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

/** Résultat de POST /auth/2fa/setup */
@Serializable
data class TwoFactorSetupDto(
    val secret: String,
    val otpAuthUrl: String
)

