package com.cyna.app.domain.model

data class TwoFactorSetupDto(
    val secret: String,
    val otpAuthUrl: String
)

