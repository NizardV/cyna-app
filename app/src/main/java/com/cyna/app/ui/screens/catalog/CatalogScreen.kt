package com.cyna.app.ui.screens.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.ui.core.components.ui.catalog.*
import dev.kindling.compose.KScreen
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KButtonVariant
import kotlinx.coroutines.launch

@Composable
fun CatalogScreen(navController: NavController) {
    KScreen(
        viewModel = viewModel<CatalogViewModel>(),
        navController = navController
    ) { state, viewModel ->
        CatalogContent(
            state = state,
            onSearchChange = viewModel::onSearchChange,
            onCategoryToggle = viewModel::onCategoryToggle,
            onMaxPriceChange = viewModel::onMaxPriceChange,
            onOnlyAvailableChange = viewModel::onOnlyAvailableChange,
            onSortChange = viewModel::onSortChange,
            onPageChange = viewModel::onPageChange,
            onResetFilters = viewModel::onResetFilters
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogContent(
    state: CatalogContracts.UiState,
    onSearchChange: (String) -> Unit,
    onCategoryToggle: (String) -> Unit,
    onMaxPriceChange: (Double?) -> Unit,
    onOnlyAvailableChange: (Boolean) -> Unit,
    onSortChange: (CatalogContracts.SortBy) -> Unit,
    onPageChange: (Int) -> Unit,
    onResetFilters: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    Row(modifier = Modifier.fillMaxSize()) {
        // --- Sidebar (Filters) ---
        if (state.loadingCategories) {
            FilterSidebarSkeleton()
        } else {
            FilterSidebar(
                categories = state.categories,
                filters = state.filters,
                onSearchChange = onSearchChange,
                onCategoryToggle = onCategoryToggle,
                onMaxPriceChange = onMaxPriceChange,
                onOnlyAvailableChange = onOnlyAvailableChange
            )
        }

        // --- Main Content ---
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
        ) {
            // Header: Total Results + Sort
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Results",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    if (!state.loadingProducts) {
                        Text(
                            text = "(${state.total} results)",
                            fontSize = 12.sp,
                            color = cs.onSurfaceVariant
                        )
                    }
                }

                // Sort Dropdown (Mocking Combobox behavior)
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (state.sortBy) {
                                CatalogContracts.SortBy.Relevance -> "Relevance"
                                CatalogContracts.SortBy.PriceAsc -> "Price: Low to High"
                                CatalogContracts.SortBy.PriceDesc -> "Price: High to Low"
                                CatalogContracts.SortBy.Name -> "Name"
                            },
                            fontSize = 12.sp
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        CatalogContracts.SortBy.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (option) {
                                            CatalogContracts.SortBy.Relevance -> "Relevance"
                                            CatalogContracts.SortBy.PriceAsc -> "Price: Low to High"
                                            CatalogContracts.SortBy.PriceDesc -> "Price: High to Low"
                                            CatalogContracts.SortBy.Name -> "Name"
                                        },
                                        fontSize = 12.sp
                                    )
                                },
                                onClick = {
                                    onSortChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Products Grid
            if (state.loadingProducts) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(9) { ProductCardSkeleton() }
                }
            } else if (state.products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No results found", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Try adjusting your filters or search terms",
                            fontSize = 12.sp,
                            color = cs.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        KButton(
                            onClick = onResetFilters,
                            variant = KButtonVariant.Link,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Reset filters", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(minSize = 250.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.products, key = { it.id }) { product ->
                            ProductCard(product = product)
                        }
                    }

                    // Pagination
                    CatalogPagination(
                        currentPage = state.currentPage,
                        totalPages = state.totalPages,
                        onPageChange = { page ->
                            onPageChange(page)
                            scope.launch { gridState.animateScrollToItem(0) }
                        },
                        modifier = Modifier.padding(top = 24.dp)
                    )

                    if (state.totalPages > 1) {
                        Text(
                            text = "Page ${state.currentPage} of ${state.totalPages}",
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            fontSize = 12.sp,
                            color = cs.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
