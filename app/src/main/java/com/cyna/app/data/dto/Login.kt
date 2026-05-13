package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequest(
    val username: String
)

@Serializable
internal data class LoginResponseDto(
    val id: String,
    val username: String,
    val score: Int,
    val guildId: String?,
    val createdAt: String,
    val updatedAt: String
)