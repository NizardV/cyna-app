package com.cyna.app.ui.core.components.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.domain.model.AccountOrder

// ── Year group ────────────────────────────────────────────────────────────────

@Composable
fun YearGroup(year: Int, orders: List<AccountOrder>, dimmed: Boolean) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (dimmed) Modifier else Modifier)
    ) {
        // Year header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = year.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (dimmed) cs.onSurfaceVariant else cs.onSurface
            )
            Text(
                text = "${orders.size} order${if (orders.size != 1) "s" else ""}",
                fontSize = 11.sp,
                color = cs.onSurfaceVariant
            )
        }

        // Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = cs.surface,
            border = BorderStroke(
                1.dp,
                cs.outline.copy(if (dimmed) .15f else .3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (dimmed) Modifier else Modifier)
        ) {
            Column {
                orders.forEachIndexed { i, order ->
                    OrderRow(order = order, isLast = i == orders.size - 1)
                }
            }
        }
    }
}

// ── Year filter chip ──────────────────────────────────────────────────────────

@Composable
fun YearFilterRow(
    years: List<Int>,
    selectedYear: String,
    onYearChange: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // "All" chip
        val allActive = selectedYear == "all"
        Surface(
            onClick = { onYearChange("all") },
            shape = RoundedCornerShape(100.dp),
            color = if (allActive) cs.primary else cs.surfaceVariant,
            modifier = Modifier.height(28.dp)
        ) {
            Text(
                "All",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 11.sp,
                fontWeight = if (allActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (allActive) cs.onPrimary else cs.onSurfaceVariant
            )
        }

        years.forEach { year ->
            val isActive = selectedYear == year.toString()
            Surface(
                onClick = { onYearChange(year.toString()) },
                shape = RoundedCornerShape(100.dp),
                color = if (isActive) cs.primary else cs.surfaceVariant,
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    year.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isActive) cs.onPrimary else cs.onSurfaceVariant
                )
            }
        }
    }
}