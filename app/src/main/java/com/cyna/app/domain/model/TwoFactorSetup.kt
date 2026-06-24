package com.cyna.app.domain.model

data class TwoFactorSetup(
    val secret: String,
    val otpAuthUrl: String
)

