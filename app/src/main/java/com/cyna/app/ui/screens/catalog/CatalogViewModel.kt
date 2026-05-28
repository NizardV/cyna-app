package com.cyna.app.ui.screens.catalog

import android.app.Application
import com.cyna.app.domain.model.Category
import com.cyna.app.domain.model.CatalogPage
import com.cyna.app.domain.model.Product
import com.cyna.app.domain.repository.CatalogRepository
import com.cyna.app.ui.core.ViewModel
import org.koin.core.component.inject

data class CatalogState(
    val products: List<Product> = emptyList(),
    val total: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val loadingProducts: Boolean = true,
    val categories: List<Category> = emptyList(),
    val loadingCategories: Boolean = true,
    val searchQuery: String = "",
    val selectedCategories: List<String> = emptyList(),
    val maxPrice: Double? = null,
    val onlyAvailable: Boolean = false,
    val sortBy: String = "relevance"
)

class CatalogViewModel(application: Application) : ViewModel<CatalogState>(CatalogState(), application) {

    // ✅ Repository instead of raw API
    private val catalogRepository: CatalogRepository by inject()

    companion object {
        const val PAGE_SIZE = 9
    }

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        fetchData(
            source = { catalogRepository.getCategories() },
            onResult = {
                onSuccess { cats -> updateState { copy(categories = cats, loadingCategories = false) } }
                onFailure  {       updateState { copy(loadingCategories = false) } }
            }
        )
    }

    fun loadProducts(page: Int = 1) {
        val s = state.value
        updateState { copy(loadingProducts = true, currentPage = page) }
        fetchData(
            source = {
                catalogRepository.getCatalogProducts(
                    query         = s.searchQuery,
                    categoryIds   = s.selectedCategories,
                    maxPrice      = s.maxPrice,
                    onlyAvailable = s.onlyAvailable,
                    sortBy        = s.sortBy,
                    page          = page,
                    pageSize      = PAGE_SIZE
                )
            },
            onResult = {
                onSuccess { pg ->
                    updateState {
                        copy(
                            products       = pg.items,
                            total          = pg.total,
                            currentPage    = pg.page,
                            totalPages     = pg.totalPages,
                            loadingProducts = false
                        )
                    }
                }
                onFailure { updateState { copy(loadingProducts = false) } }
            }
        )
    }

    fun onSearchChange(q: String)       { updateState { copy(searchQuery = q) };          loadProducts(1) }
    fun onCategoryToggle(id: String)    {
        val next = state.value.selectedCategories.let { if (id in it) it - id else it + id }
        updateState { copy(selectedCategories = next) }
        loadProducts(1)
    }
    fun onMaxPriceChange(price: Double?) { updateState { copy(maxPrice = price) };         loadProducts(1) }
    fun onAvailableToggle(v: Boolean)   { updateState { copy(onlyAvailable = v) };        loadProducts(1) }
    fun onSortChange(sort: String)      { updateState { copy(sortBy = sort) };             loadProducts(1) }
    fun onPageChange(page: Int)         = loadProducts(page)

    fun resetFilters() {
        updateState {
            copy(
                searchQuery        = "",
                selectedCategories = emptyList(),
                maxPrice           = null,
                onlyAvailable      = false,
                sortBy             = "relevance"
            )
        }
        loadProducts(1)
    }
}