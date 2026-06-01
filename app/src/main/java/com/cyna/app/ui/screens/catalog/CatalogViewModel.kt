package com.cyna.app.ui.screens.catalog

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.cyna.app.domain.model.CatalogPage
import com.cyna.app.domain.model.CatalogProduct
import com.cyna.app.domain.model.Category
import com.cyna.app.domain.repository.CatalogRepository
import dev.kindling.compose.KViewModel
import dev.kindling.utils.Debouncer
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.milliseconds

// ─────────────────────────────────────────────
//  Contracts
// ─────────────────────────────────────────────

interface CatalogContracts {
    enum class SortBy(val apiValue: String) {
        Relevance("relevance"),
        PriceAsc("price_asc"),
        PriceDesc("price_desc"),
        Name("name")
    }

    data class Filters(
        val search: String = "",
        val categories: List<String> = emptyList(),
        val maxPrice: Double? = null,
        val onlyAvailable: Boolean = false
    )

    data class UiState(
        // Products
        val products: List<CatalogProduct> = emptyList(),
        val total: Int = 0,
        val totalPages: Int = 1,
        val currentPage: Int = 1,
        val loadingProducts: Boolean = true,

        // Categories
        val categories: List<Category> = emptyList(),
        val loadingCategories: Boolean = true,

        // Filters
        val filters: Filters = Filters(),

        // Sort (Combobox in React)
        val sortBy: SortBy = SortBy.Relevance,

        // Error
        val error: String? = null
    )
}

// ─────────────────────────────────────────────
//  ViewModel
// ─────────────────────────────────────────────

class CatalogViewModel(application: Application) :
    KViewModel<CatalogContracts.UiState>(CatalogContracts.UiState(), application) {

    private val catalogRepository: CatalogRepository by inject()

    // Mirrors useDebounce(filters.search, 700) — search debounce
    private val searchDebouncer = Debouncer<String>(viewModelScope, 700.milliseconds)

    // Mirrors useDebounce(filters.categories, 1000) — categories debounce
    private val categoriesDebouncer = Debouncer<List<String>>(viewModelScope, 1_000.milliseconds)

    // Mirrors useDebounce(filters.maxPrice, 500) — price debounce
    private val priceDebouncer = Debouncer<Double?>(viewModelScope, 500.milliseconds)

    // Pending debounced values (flushed into the fetch call)
    private var debouncedSearch: String = ""
    private var debouncedCategories: List<String> = emptyList()
    private var debouncedMaxPrice: Double? = null

    init {
        loadCategories()

        searchDebouncer.onDebounced { q ->
            debouncedSearch = q
            fetchProducts(resetPage = true)
        }
        categoriesDebouncer.onDebounced { cats ->
            debouncedCategories = cats
            fetchProducts(resetPage = true)
        }
        priceDebouncer.onDebounced { price ->
            debouncedMaxPrice = price
            fetchProducts(resetPage = true)
        }

        fetchProducts()
    }

    // ── Categories ────────────────────────────────────────────────────────────

    private fun loadCategories() {
        fetchData(
            source = { catalogRepository.getCategories() },
            onResult = {
                onSuccess { cats ->
                    updateState { copy(categories = cats, loadingCategories = false) }
                }
                onFailure {
                    updateState { copy(loadingCategories = false) }
                }
            }
        )
    }

    // ── Products ──────────────────────────────────────────────────────────────

    /**
     * Fetches the current page with the current debounced filter values.
     * Mirrors the useEffect in catalog.jsx that depends on all debounced values.
     */
    private fun fetchProducts(resetPage: Boolean = false) {
        val page = if (resetPage) 1 else state.value.currentPage
        if (resetPage) updateState { copy(currentPage = 1) }

        updateState { copy(loadingProducts = true, error = null) }

        fetchData(
            source = {
                val s = state.value
                catalogRepository.getCatalogProducts(
                    query         = debouncedSearch,
                    categoryIds   = debouncedCategories,
                    maxPrice      = debouncedMaxPrice,
                    onlyAvailable = s.filters.onlyAvailable,
                    sortBy        = s.sortBy.apiValue,
                    page          = page,
                    pageSize      = PAGE_SIZE
                )
            },
            onResult = {
                onSuccess { page: CatalogPage ->
                    updateState {
                        copy(
                            products        = page.items,
                            total           = page.total,
                            totalPages      = page.totalPages,
                            currentPage     = page.page,
                            loadingProducts = false
                        )
                    }
                }
                onFailure { e ->
                    updateState {
                        copy(
                            loadingProducts = false,
                            error           = e.message ?: "Failed to load products"
                        )
                    }
                }
            }
        )
    }

    // ── Public actions — mirrors handleFilterChange + sort/page setters ───────

    /** Mirrors handleFilterChange({ search: ... }) + resets page to 1 */
    fun onSearchChange(q: String) {
        updateState { copy(filters = filters.copy(search = q), currentPage = 1) }
        searchDebouncer.emit(q)
    }

    /** Mirrors handleFilterChange({ categories: ... }) */
    fun onCategoryToggle(categoryId: String) {
        val current = state.value.filters.categories
        val next = if (categoryId in current) current - categoryId else current + categoryId
        updateState { copy(filters = filters.copy(categories = next), currentPage = 1) }
        categoriesDebouncer.emit(next)
    }

    /** Mirrors onChange({ maxPrice: ... }) on the range slider */
    fun onMaxPriceChange(price: Double?) {
        updateState { copy(filters = filters.copy(maxPrice = price), currentPage = 1) }
        priceDebouncer.emit(price)
    }

    /** Mirrors onChange({ onlyAvailable: ... }) — no debounce needed */
    fun onOnlyAvailableChange(only: Boolean) {
        updateState { copy(filters = filters.copy(onlyAvailable = only), currentPage = 1) }
        fetchProducts(resetPage = true)
    }

    /** Mirrors setSortBy + setCurrentPage(1) in the Combobox handler */
    fun onSortChange(sort: CatalogContracts.SortBy) {
        updateState { copy(sortBy = sort, currentPage = 1) }
        fetchProducts(resetPage = true)
    }

    /** Mirrors onPageChange — scrolls to top is handled by the UI */
    fun onPageChange(page: Int) {
        updateState { copy(currentPage = page) }
        fetchProducts()
    }

    /** Mirrors the "Reset filters" button in the empty state */
    fun onResetFilters() {
        debouncedSearch = ""
        debouncedCategories = emptyList()
        debouncedMaxPrice = null
        updateState {
            copy(
                filters     = CatalogContracts.Filters(),
                sortBy      = CatalogContracts.SortBy.Relevance,
                currentPage = 1
            )
        }
        fetchProducts(resetPage = true)
    }

    companion object {
        const val PAGE_SIZE = 9
    }
}