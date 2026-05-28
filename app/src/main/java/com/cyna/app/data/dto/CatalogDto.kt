package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CatalogPageDto(
    val items: List<ProductDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)