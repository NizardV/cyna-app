package com.cyna.app.ui.core.components.ui.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.domain.model.CatalogProduct
import dev.kindling.core.components.*
import dev.kindling.utils.method.formatPrice

@Composable
fun ProductCard(
    product: CatalogProduct,
    modifier: Modifier = Modifier
) {
    val isAvailable = product.status == "available"
    val cs = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(cs.outline.copy(alpha = 0.2f))
        )
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = cs.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        KBadge(
                            variant = getStatusBadgeVariant(product.status)
                        ) {
                            Text(
                                text = product.status.replace("_", " ").capitalize(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (isAvailable) {
                            Text(
                                text = buildString {
                                    append(formatPrice(product.price))
                                    append(" ")
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = cs.primary
                            )
                        }
                    }

                    KButton(
                        onClick = { /* Navigate to details */ },
                        size = KButtonSize.Sm,
                        variant = if (isAvailable) KButtonVariant.Default else KButtonVariant.Outline,
                        enabled = isAvailable
                    ) {
                        Text("Details", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun getStatusBadgeVariant(status: String): KBadgeVariant {
    val cs = MaterialTheme.colorScheme
    return when (status) {
        "available" -> KBadgeVariant(
            bg = { Color(0xFF16A34A).copy(alpha = 0.1f) },
            fg = { Color(0xFF16A34A) }
        )
        "unavailable", "out_of_stock" -> KBadgeVariant(
            bg = { cs.error.copy(alpha = 0.1f) },
            fg = { cs.error }
        )
        else -> KBadgeVariant(
            bg = { cs.surfaceVariant },
            fg = { cs.onSurfaceVariant }
        )
    }
}

@Composable
fun ProductCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Skeleton(modifier = Modifier.fillMaxWidth().height(160.dp))
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Skeleton(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
                Skeleton(modifier = Modifier.fillMaxWidth().height(12.dp))
                Skeleton(modifier = Modifier.fillMaxWidth().height(12.dp))
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Skeleton(modifier = Modifier.width(60.dp).height(18.dp))
                        Skeleton(modifier = Modifier.width(80.dp).height(20.dp))
                    }
                    Skeleton(modifier = Modifier.width(70.dp).height(32.dp))
                }
            }
        }
    }
}
