package com.cyna.app.ui.core.components.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.cyna.app.domain.model.Subscription
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KButtonSize
import dev.kindling.core.components.KButtonVariant
import dev.kindling.utils.method.formatDate

// ── Subscription item ─────────────────────────────────────────────────────────

@Composable
fun SubscriptionRow(sub: Subscription, onCancel: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = cs.primary.copy(.05f),
        border = BorderStroke(1.dp, cs.primary.copy(.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.productName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${sub.quantity} users · Renews ${formatDate(sub.endsAt)}",
                    fontSize = 11.sp, color = cs.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            KButton("Cancel", onClick = onCancel, variant = KButtonVariant.Destructive, size = KButtonSize.Sm)
        }
    }
}
