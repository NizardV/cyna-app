package com.cyna.app.ui.core.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.kindling.core.components.KInput
import dev.kindling.core.components.KLabel

// ── Labeled field ─────────────────────────────────────────────────────────────

@Composable
fun FieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        KLabel(label)
        if (trailingContent != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                KInput(
                    value = value,
                    onValueChange = onValueChange,
                    isPassword = isPassword,
                    modifier = Modifier.weight(1f)
                )
                trailingContent()
            }
        } else {
            KInput(value = value, onValueChange = onValueChange, isPassword = isPassword)
        }
    }
}