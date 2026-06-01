package com.cyna.app.data.repository

import com.cyna.app.data.remote.CatalogAPI
import com.cyna.app.domain.model.Category
import com.cyna.app.domain.model.CatalogPage
import com.cyna.app.domain.model.CatalogProduct
import com.cyna.app.domain.repository.CatalogRepository

internal class CatalogRepositoryImpl(
    private val catalogAPI: CatalogAPI
) : CatalogRepository {

    override suspend fun getCategories(): List<Category> =
        catalogAPI.getCategories().map { dto ->
            Category(
                id          = dto.id,
                name        = dto.name,
                description = dto.description,
                image       = dto.image
            )
        }

    override suspend fun getCatalogProducts(
        query: String,
        categoryIds: List<String>,
        maxPrice: Double?,
        onlyAvailable: Boolean,
        sortBy: String,
        page: Int,
        pageSize: Int
    ): CatalogPage {
        val dto = catalogAPI.getCatalogProducts(
            query         = query,
            categoryIds   = categoryIds,
            maxPrice      = maxPrice,
            onlyAvailable = onlyAvailable,
            sortBy        = sortBy,
            page          = page,
            pageSize      = pageSize
        )
        return CatalogPage(
            items = dto.items.map { p ->
                CatalogProduct(
                    id = p.id,
                    categoryId = p.categoryId,
                    imageUrl = p.imageUrl,
                    name = p.name,
                    status = p.status,
                    description = p.description,
                    price = p.price,
                    billingPeriod = p.billingPeriod,
                    discountPercent = p.discountPercent
                )
            },
            total      = dto.total,
            page       = dto.page,
            totalPages = dto.totalPages
        )
    }
}
