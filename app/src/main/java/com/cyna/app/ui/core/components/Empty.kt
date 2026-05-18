package com.diiage.template.ui.core.components.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  EmptyMedia variant
// ─────────────────────────────────────────────

enum class EmptyMediaVariant {
    Icon,    // circle bg + icon (default)
    Image,   // raw image / illustration slot
    Avatar   // for avatar / avatar-group
}

// ─────────────────────────────────────────────
//  Empty  (root)
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style Empty state component.
 *
 * Composition:
 * ```
 * Empty
 * ├── EmptyHeader
 * │   ├── EmptyMedia
 * │   ├── EmptyTitle
 * │   └── EmptyDescription
 * └── EmptyContent
 * ```
 *
 * Usage — default:
 * ```
 * Empty {
 *     EmptyHeader {
 *         EmptyMedia { Icon(Icons.Outlined.FolderOpen, null) }
 *         EmptyTitle("No Projects Yet")
 *         EmptyDescription("You haven't created any projects yet.\nGet started by creating your first one.")
 *     }
 *     EmptyContent {
 *         Button(text = "Create Project", onClick = { })
 *     }
 * }
 * ```
 *
 * Usage — outline variant:
 * ```
 * Empty(outlined = true) { … }
 * ```
 *
 * Usage — with background fill:
 * ```
 * Empty(showBackground = true) { … }
 * ```
 */
@Composable
fun Empty(
    modifier: Modifier    = Modifier,
    outlined: Boolean     = false,
    showBackground: Boolean = false,
    shape: Shape          = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier            = modifier
            .fillMaxWidth()
            .then(
                if (showBackground)
                    Modifier
                        .clip(shape)
                        .background(cs.surfaceVariant.copy(alpha = 0.4f))
                else Modifier
            )
            .then(
                if (outlined)
                    Modifier
                        .clip(shape)
                        .border(1.dp, cs.outline, shape)
                else Modifier
            )
            .padding(if (outlined || showBackground) 32.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        content             = content
    )
}

// ─────────────────────────────────────────────
//  EmptyHeader
// ─────────────────────────────────────────────

/**
 * Container for [EmptyMedia], [EmptyTitle], and [EmptyDescription].
 */
@Composable
fun EmptyHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content             = content
    )
}

// ─────────────────────────────────────────────
//  EmptyMedia
// ─────────────────────────────────────────────

/**
 * Displays the visual centrepiece of the empty state.
 *
 * - [EmptyMediaVariant.Icon]   → rounded square / circle background with an icon inside
 * - [EmptyMediaVariant.Image]  → raw slot, no background (use for illustrations)
 * - [EmptyMediaVariant.Avatar] → same as Image but circular clip
 *
 * Usage:
 * ```
 * EmptyMedia(variant = EmptyMediaVariant.Icon) {
 *     Icon(Icons.Outlined.FolderOpen, contentDescription = null)
 * }
 * ```
 */
@Composable
fun EmptyMedia(
    modifier: Modifier         = Modifier,
    variant: EmptyMediaVariant = EmptyMediaVariant.Icon,
    size: Dp                   = 56.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val boxModifier = when (variant) {
        EmptyMediaVariant.Icon -> modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(cs.surfaceVariant)
            .padding(12.dp)

        EmptyMediaVariant.Avatar -> modifier
            .size(size)
            .clip(CircleShape)

        EmptyMediaVariant.Image -> modifier
            .size(size)
    }

    Box(
        modifier        = boxModifier,
        contentAlignment = Alignment.Center,
        content         = content
    )
}

// ─────────────────────────────────────────────
//  EmptyTitle
// ─────────────────────────────────────────────

/**
 * The primary heading of the empty state.
 */
@Composable
fun EmptyTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text      = text,
        modifier  = modifier,
        fontSize  = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color     = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
}

// ─────────────────────────────────────────────
//  EmptyDescription
// ─────────────────────────────────────────────

/**
 * Supporting description text beneath the title.
 */
@Composable
fun EmptyDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text      = text,
        modifier  = modifier.padding(horizontal = 16.dp),
        fontSize  = 14.sp,
        fontWeight = FontWeight.Normal,
        color     = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
    )
}

// ─────────────────────────────────────────────
//  EmptyContent
// ─────────────────────────────────────────────

/**
 * Action area below the header — buttons, inputs, links, etc.
 */
@Composable
fun EmptyContent(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Spacer(Modifier.height(8.dp))
    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        content             = content
    )
}

// ─────────────────────────────────────────────
//  Preset convenience composables
// ─────────────────────────────────────────────

/**
 * Ready-to-use empty state with an icon, title, description, and a primary CTA.
 *
 * ```
 * EmptyState(
 *     icon        = Icons.Outlined.FolderOpen,
 *     title       = "No projects yet",
 *     description = "Create your first project to get started.",
 *     actionLabel = "Create Project",
 *     onAction    = { }
 * )
 * ```
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier        = Modifier,
    outlined: Boolean         = false,
    showBackground: Boolean   = false,
    actionLabel: String?      = null,
    secondaryActionLabel: String? = null,
    onAction: (() -> Unit)?   = null,
    onSecondaryAction: (() -> Unit)? = null
) {
    Empty(modifier = modifier, outlined = outlined, showBackground = showBackground) {
        EmptyHeader {
            Spacer(Modifier.height(8.dp))
            EmptyMedia(variant = EmptyMediaVariant.Icon) {
                Icon(
                    imageVector       = icon,
                    contentDescription = null,
                    tint              = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier          = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(4.dp))
            EmptyTitle(text = title)
            EmptyDescription(text = description)
        }

        if (actionLabel != null || secondaryActionLabel != null) {
            EmptyContent {
                if (actionLabel != null && onAction != null) {
                    Button(
                        text    = actionLabel,
                        onClick = onAction,
                        variant = ButtonVariant.Default
                    )
                }
                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    Button(
                        text    = secondaryActionLabel,
                        onClick = onSecondaryAction,
                        variant = ButtonVariant.Outline
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
