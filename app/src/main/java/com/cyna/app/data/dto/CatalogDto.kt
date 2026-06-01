package com.cyna.app.data.dto

import kotlinx.serialization.Serializable


@Serializable
internal data class CatalogPageDto(
    val items: List<CatalogProductDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

@Serializable
internal data class CatalogProductDto(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val description: String,
    val price: Double,
    val billingPeriod: String,
    val discountPercent: Double?,
)