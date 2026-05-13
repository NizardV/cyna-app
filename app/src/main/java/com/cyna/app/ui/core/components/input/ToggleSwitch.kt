package com.cyna.app.ui.core.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

/**
 * A simple toggle switch that switches between primary and secondary states
 * The entire track background changes color based on the selection
 *
 * @param isPrimarySelected True if primary is selected, false if secondary is selected
 * @param onToggle Callback when toggle state changes
 * @param modifier Modifier for the component
 * @param enabled Whether the toggle is enabled
 * @param showLabels Whether to show labels for each state
 */
@Composable
fun PrimarySecondaryToggle(
    isPrimarySelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showLabels: Boolean = true
) {
    val trackWidth = 52.dp
    val thumbSize = 24.dp
    val padding = 2.dp

    // Animate the track background color
    val trackColor by animateColorAsState(
        targetValue = if (enabled) {
            if (isPrimarySelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "trackColorAnimation"
    )

    // Animate the thumb position
    val thumbOffset by animateDpAsState(
        targetValue = if (isPrimarySelected) {
            padding
        } else {
            trackWidth - thumbSize - padding
        },
        animationSpec = tween(durationMillis = 300),
        label = "thumbAnimation"
    )

    val thumbColor = if (enabled) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showLabels) {
            Text(
                text = "Primaire",
                style = MaterialTheme.typography.labelMedium,
                color = if (isPrimarySelected && enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 0.6f else 0.3f
                    )
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(trackColor)
                .clickable(enabled = enabled) {
                    onToggle(!isPrimarySelected)
                }
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(thumbColor)
                    .shadow(2.dp, CircleShape)
                    .align(Alignment.CenterStart)
                    .offset(x = thumbOffset)
            )
        }

        if (showLabels) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Secondaire",
                style = MaterialTheme.typography.labelMedium,
                color = if (!isPrimarySelected && enabled) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 0.6f else 0.3f
                    )
                }
            )
        }
    }
}

// Alternative version with more prominent background color
@Composable
fun PrimarySecondaryToggleVariant(
    isPrimarySelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showLabels: Boolean = true
) {
    val trackWidth = 52.dp
    val thumbSize = 24.dp
    val padding = 2.dp

    // More prominent track color animation
    val trackColor by animateColorAsState(
        targetValue = if (enabled) {
            if (isPrimarySelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "trackColorAnimation"
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (isPrimarySelected) {
            padding
        } else {
            trackWidth - thumbSize - padding
        },
        animationSpec = tween(durationMillis = 300),
        label = "thumbAnimation"
    )

    val thumbColor = if (enabled) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showLabels) {
            Text(
                text = "Primaire",
                style = MaterialTheme.typography.labelMedium,
                color = if (isPrimarySelected && enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 0.6f else 0.3f
                    )
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(trackColor)
                .clickable(enabled = enabled) {
                    onToggle(!isPrimarySelected)
                }
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(thumbColor)
                    .shadow(2.dp, CircleShape)
                    .align(Alignment.CenterStart)
                    .offset(x = thumbOffset)
            )
        }

        if (showLabels) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Secondaire",
                style = MaterialTheme.typography.labelMedium,
                color = if (!isPrimarySelected && enabled) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (enabled) 0.6f else 0.3f
                    )
                }
            )
        }
    }
}
