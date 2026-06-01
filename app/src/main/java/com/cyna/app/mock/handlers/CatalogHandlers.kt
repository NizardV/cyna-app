package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.CatalogPageDto
import com.cyna.app.data.dto.CatalogProductDto
import com.cyna.app.mock.factories.*
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*

// ---------------------------------------------------------------------------
// In-memory stores — shared between category and catalog handlers
// ---------------------------------------------------------------------------

private val _categories: List<MockCategory> =
    MockFactories.makeMany(6) { MockFactories.makeCategory() }

private val _products: MutableList<MockProduct> =
    MockFactories.makeMany(40) {
        MockFactories.makeProduct(categoryId = _categories.random().id)
    }.toMutableList()

// ---------------------------------------------------------------------------
// Category handlers — mirrors categories.js
// ---------------------------------------------------------------------------

val categoryHandlers: List<MockHandler> = listOf(
    MockHandler(
        method = HttpMethod.Get,
        path = "/categories",
        resolver = { _, _ -> _categories }
    ),
    MockHandler(
        method = HttpMethod.Get,
        path = "/categories/:id",
        resolver = { params, _ -> _categories.find { it.id == params["id"] } }
    ),
)

// ---------------------------------------------------------------------------
// Catalog handlers — with server-side filtering, sorting, pagination
// Mirrors pages/catalog.jsx API logic
// ---------------------------------------------------------------------------

private const val CATALOG_PAGE_SIZE = 9

val catalogHandlers: List<MockHandler> = listOf(
    MockHandler(
        method = HttpMethod.Get,
        path = "/catalog/products",
        resolver = { params, _ ->
            val q = (params["q"] ?: "").lowercase()
            val catIds = params["categoryIds"]
                ?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            val maxPrice = params["maxPrice"]?.toDoubleOrNull()
            val onlyAvailable = params["available"] == "true"
            val sortBy = params["sortBy"] ?: "relevance"
            val page = (params["page"]?.toIntOrNull() ?: 1).coerceAtLeast(1)
            val pageSize = (params["pageSize"]?.toIntOrNull() ?: CATALOG_PAGE_SIZE).coerceAtLeast(1)

            var filtered = _products.filter { p ->
                if (q.isNotEmpty() && !p.name.lowercase().contains(q) &&
                    !p.description.lowercase().contains(q)) return@filter false
                if (catIds.isNotEmpty() && p.categoryId !in catIds) return@filter false
                if (maxPrice != null && p.priceMonthly > maxPrice) return@filter false
                if (onlyAvailable && p.status != "available") return@filter false
                true
            }

            filtered = when (sortBy) {
                "price_asc"  -> filtered.sortedBy { it.priceMonthly }
                "price_desc" -> filtered.sortedByDescending { it.priceMonthly }
                "name"       -> filtered.sortedBy { it.name }
                else         -> filtered
            }

            val total = filtered.size
            val totalPages = maxOf(1, Math.ceil(total.toDouble() / pageSize).toInt())
            val safePage = page.coerceAtMost(totalPages)
            val offset = (safePage - 1) * pageSize
            
            val items = filtered.drop(offset).take(pageSize).map { p ->
                CatalogProductDto(
                    id = p.id,
                    categoryId = p.categoryId,
                    imageUrl = p.imageUrl,
                    name = p.name,
                    status = p.status,
                    description = p.description,
                    price = p.priceMonthly,
                    billingPeriod = "monthly",
                    discountPercent = null
                )
            }

            CatalogPageDto(
                items = items,
                total = total,
                page = safePage,
                pageSize = pageSize,
                totalPages = totalPages
            )
        }
    ),
)
