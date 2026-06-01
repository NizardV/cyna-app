package com.cyna.app.ui.core.components.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.domain.model.Category
import com.cyna.app.ui.screens.catalog.CatalogContracts
import dev.kindling.core.components.KInput
import dev.kindling.core.components.Skeleton

@Composable
fun FilterSidebar(
    categories: List<Category>,
    filters: CatalogContracts.Filters,
    onSearchChange: (String) -> Unit,
    onCategoryToggle: (String) -> Unit,
    onMaxPriceChange: (Double?) -> Unit,
    onOnlyAvailableChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Filters",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Search
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Search",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            KInput(
                value = filters.search,
                onValueChange = onSearchChange,
                placeholder = "Search services...",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Categories
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Categories",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = category.id in filters.categories,
                            onCheckedChange = { onCategoryToggle(category.id) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = category.name,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Budget
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Budget",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = (filters.maxPrice ?: 1000.0).toFloat(),
                onValueChange = { onMaxPriceChange(it.toDouble()) },
                valueRange = 0f..1000f,
                steps = 100,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0€", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = if (filters.maxPrice == null || filters.maxPrice == 1000.0) "Max" else "${filters.maxPrice?.toInt()}€",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Availability
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                checked = filters.onlyAvailable,
                onCheckedChange = onOnlyAvailableChange,
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Only available",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FilterSidebarSkeleton() {
    Column(
        modifier = Modifier
            .width(240.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Skeleton(modifier = Modifier.width(80.dp).height(16.dp))
        repeat(4) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Skeleton(modifier = Modifier.width(60.dp).height(12.dp))
                Skeleton(modifier = Modifier.fillMaxWidth().height(36.dp))
            }
        }
    }
}
