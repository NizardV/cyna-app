package com.cyna.app.domain.repository

import com.cyna.app.domain.model.Category
import com.cyna.app.domain.model.CatalogPage

interface CatalogRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getCatalogProducts(
        query: String = "",
        categoryIds: List<String> = emptyList(),
        maxPrice: Double? = null,
        onlyAvailable: Boolean = false,
        sortBy: String = "relevance",
        page: Int = 1,
        pageSize: Int = 9
    ): CatalogPage
}