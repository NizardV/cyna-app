package com.cyna.app.domain.model

data class CatalogPage(
    val items: List<CatalogProduct>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)

data class CatalogProduct(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val status: String,
    val description: String,
    val price: Double,
    val billingPeriod: String,
    val discountPercent: Double?,
)