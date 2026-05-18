package com.diiage.template.ui.core.components.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  InputOTP
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style InputOTP — a one-time-password / PIN field.
 *
 * Renders [length] individual digit boxes with a grouped separator option.
 * Internally uses a single hidden [BasicTextField] so the keyboard and
 * cursor work naturally.
 *
 * Usage:
 * ```
 * var otp by remember { mutableStateOf("") }
 *
 * InputOTP(
 *     value        = otp,
 *     onValueChange = { if (it.length <= 6) otp = it },
 *     length       = 6
 * )
 *
 * // With group separator (e.g. 3-3)
 * InputOTP(
 *     value        = otp,
 *     onValueChange = { if (it.length <= 6) otp = it },
 *     length       = 6,
 *     groups       = listOf(3, 3)
 * )
 *
 * // 4-digit PIN
 * InputOTP(value = pin, onValueChange = { if (it.length <= 4) pin = it }, length = 4)
 * ```
 */
@Composable
fun InputOTP(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier  = Modifier,
    length: Int         = 6,
    groups: List<Int>?  = null,    // e.g. listOf(3,3) → "xxx–xxx"
    enabled: Boolean    = true,
    isError: Boolean    = false,
    cellSize: Dp        = 44.dp,
    cellSpacing: Dp     = 8.dp,
    separator: @Composable () -> Unit = {
        Text(
            "–",
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
) {
    val cs = MaterialTheme.colorScheme
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Hidden real text field — collects the input
        BasicTextField(
            value          = value,
            onValueChange  = { new ->
                if (enabled) {
                    val filtered = new.filter { it.isDigit() }.take(length)
                    onValueChange(filtered)
                }
            },
            modifier       = Modifier
                .size(1.dp)                         // invisible but tappable
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            enabled        = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush    = SolidColor(Color.Transparent)
        )

        // Visual cells
        Row(
            horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            verticalAlignment     = Alignment.CenterVertically,
            modifier              = Modifier.noRippleClickable { focusRequester.requestFocus() }
        ) {
            if (groups == null) {
                // No groups — just render all cells
                repeat(length) { index ->
                    OTPCell(
                        char      = value.getOrNull(index),
                        isActive  = isFocused && index == value.length,
                        isError   = isError,
                        enabled   = enabled,
                        size      = cellSize
                    )
                }
            } else {
                // Grouped with separators
                var globalIndex = 0
                groups.forEachIndexed { groupIdx, groupSize ->
                    repeat(groupSize) { _ ->
                        OTPCell(
                            char      = value.getOrNull(globalIndex),
                            isActive  = isFocused && globalIndex == value.length,
                            isError   = isError,
                            enabled   = enabled,
                            size      = cellSize
                        )
                        globalIndex++
                    }
                    if (groupIdx < groups.lastIndex) {
                        separator()
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Individual cell
// ─────────────────────────────────────────────

@Composable
private fun OTPCell(
    char: Char?,
    isActive: Boolean,
    isError: Boolean,
    enabled: Boolean,
    size: Dp
) {
    val cs = MaterialTheme.colorScheme

    val borderColor = when {
        isError  -> cs.error
        isActive -> cs.primary
        else     -> cs.outline
    }

    Box(
        modifier        = Modifier
            .size(size)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (char != null) {
            Text(
                text       = char.toString(),
                fontSize   = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign  = TextAlign.Center,
                color      = if (enabled) cs.onBackground
                             else cs.onSurface.copy(alpha = 0.38f)
            )
        } else if (isActive) {
            // Blinking cursor indicator
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .background(cs.primary, RoundedCornerShape(1.dp))
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
            indication        = null,
            onClick           = onClick
        )
    )

private fun Modifier.background(color: Color, shape: androidx.compose.ui.graphics.Shape) =
    this.then(
        Modifier.clip(shape).then(Modifier.background(color))
    )
