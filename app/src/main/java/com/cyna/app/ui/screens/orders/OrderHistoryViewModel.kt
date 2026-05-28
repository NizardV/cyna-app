package com.cyna.app.ui.screens.orders

import android.app.Application
import com.cyna.app.data.remote.OrderAPI
import com.cyna.app.data.remote.UserAPI
import com.cyna.app.domain.model.AccountOrder
import com.cyna.app.domain.model.User
import com.cyna.app.ui.core.ViewModel
import org.koin.core.component.inject

// ── Domain model ─────────────────────────────────────────────────────────────

// Add to domain/model/DomainModels.kt:
// data class AccountOrder(
//     val id: String,
//     val status: String,
//     val statusLabel: String,
//     val productName: String,
//     val total: Double,
//     val type: String,
//     val paymentLast4: String,
//     val paymentMethod: String,
//     val invoiceUrl: String?,
//     val createdAt: String
// )

// ── State ─────────────────────────────────────────────────────────────────────

data class OrderHistoryState(
    val orders: List<AccountOrder> = emptyList(),
    val user: User? = null,
    val loading: Boolean = true,
    val loadingUser: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedYear: String = "all"
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class OrderHistoryViewModel(application: Application) :
    ViewModel<OrderHistoryState>(OrderHistoryState(), application) {

    private val orderAPI: OrderAPI by inject()
    private val userAPI: UserAPI by inject()

    init {
        load()
    }

    private fun load() {
        fetchData(
            source = {
                val orders = orderAPI.getAccountOrders()
                val user = runCatching { userAPI.getMe() }.getOrNull()
                Pair(orders, user)
            },
            onResult = {
                onSuccess { (orders, userDto) ->
                    val domainOrders = orders.map { o ->
                        AccountOrder(
                            id = o.id,
                            status = o.status,
                            statusLabel = o.statusLabel,
                            productName = o.productName,
                            total = o.total,
                            type = o.type,
                            paymentLast4 = o.paymentLast4,
                            paymentMethod = o.paymentMethod,
                            invoiceUrl = o.invoiceUrl,
                            createdAt = o.createdAt
                        )
                    }
                    val user = userDto?.let {
                        User(it.id, it.name, it.email, it.role, it.isConfirmed)
                    }
                    updateState {
                        copy(
                            orders = domainOrders,
                            user = user,
                            loading = false,
                            loadingUser = false
                        )
                    }
                }
                onFailure { e ->
                    updateState {
                        copy(
                            loading = false,
                            loadingUser = false,
                            error = e.message ?: "Failed to load orders"
                        )
                    }
                }
            }
        )
    }

    fun onSearchChange(q: String) = updateState { copy(searchQuery = q) }
    fun onYearChange(year: String) = updateState { copy(selectedYear = year) }
    fun retry() {
        updateState { copy(loading = true, loadingUser = true, error = null) }
        load()
    }

    val filteredOrders: List<AccountOrder>
        get() {
            val s = state.value
            return s.orders.filter { order ->
                val matchSearch = s.searchQuery.isBlank() ||
                        order.productName.contains(s.searchQuery, ignoreCase = true)
                val matchYear = s.selectedYear == "all" ||
                        getYear(order.createdAt).toString() == s.selectedYear
                matchSearch && matchYear
            }
        }
}

private fun getYear(isoDate: String): Int = runCatching {
    java.time.Instant.parse(isoDate)
        .atZone(java.time.ZoneId.systemDefault())
        .year
}.getOrDefault(0)