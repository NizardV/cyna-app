package com.cyna.app.ui.core.components.ui.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CatalogPagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalPages <= 1) return

    val pages = buildPageRange(currentPage, totalPages)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous
        IconButton(
            onClick = { onPageChange((currentPage - 1).coerceAtLeast(1)) },
            enabled = currentPage > 1
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous")
        }

        pages.forEachIndexed { index, page ->
            if (page == null) {
                Text(
                    "...",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val isSelected = page == currentPage
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .clickable { onPageChange(page) }
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = page.toString(),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Next
        IconButton(
            onClick = { onPageChange((currentPage + 1).coerceAtMost(totalPages)) },
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
        }
    }
}

private fun buildPageRange(current: Int, total: Int): List<Int?> {
    val delta = 2
    val range = mutableListOf<Int>()
    val rangeWithDots = mutableListOf<Int?>()
    var l: Int? = null

    for (i in 1..total) {
        if (i == 1 || i == total || (i >= current - delta && i <= current + delta)) {
            range.add(i)
        }
    }

    for (i in range) {
        if (l != null) {
            if (i - l == 2) {
                rangeWithDots.add(l + 1)
            } else if (i - l != 1) {
                rangeWithDots.add(null)
            }
        }
        rangeWithDots.add(i)
        l = i
    }

    return rangeWithDots
}
