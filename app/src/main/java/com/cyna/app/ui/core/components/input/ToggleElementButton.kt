package com.cyna.app.ui.core.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ToggleElementButton(
    valueA: String,
    valueB: String,
    currentValue: String,
    onValueChange: (String) -> Unit
) {
    val isASelected = currentValue == valueA

    // Couleurs animées pour chaque segment
    val colorA by animateColorAsState(
        if (isASelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.tertiary
    )
    val textColorA by animateColorAsState(
        if (isASelected) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.primary
    )

    val colorB by animateColorAsState(
        if (!isASelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.tertiary
    )
    val textColorB by animateColorAsState(
        if (!isASelected) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.primary
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {

        // Segment A
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorA)
                .clickable { onValueChange(valueA) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valueA,
                color = textColorA
            )
        }

        // Segment B
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorB)
                .clickable { onValueChange(valueB) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valueB,
                color = textColorB
            )
        }
    }
}