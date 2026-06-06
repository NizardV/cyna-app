package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val displayOrder: Int,
    val createdAt: String
)