@file:Suppress("UnusedImport")

package com.diiage.template.ui.screens.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diiage.template.ui.core.components.ui.*
import com.diiage.template.ui.core.theme.AppTheme
import java.time.LocalDate

// ─────────────────────────────────────────────────────────────────────────────
//  Preview wrapper — applies AppTheme + standard padding
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PreviewSurface(
    darkTheme: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    AppTheme {
        Surface(
            color    = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier            = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content             = content
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers re-used inside previews
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        fontSize   = 11.sp,
        fontWeight = FontWeight.Medium,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier   = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun Row2Preview(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        content               = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Button
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Button — light", showBackground = true, widthDp = 360)
@Preview(name = "Button — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewButtons() {
    PreviewSurface {
        SectionLabel("Variants")
        Row2Preview {
            Button("Default",     onClick = {}, modifier = Modifier.weight(1f))
            Button("Outline",     onClick = {}, variant = ButtonVariant.Outline,     modifier = Modifier.weight(1f))
        }
        Row2Preview {
            Button("Secondary",   onClick = {}, variant = ButtonVariant.Secondary,   modifier = Modifier.weight(1f))
            Button("Destructive", onClick = {}, variant = ButtonVariant.Destructive, modifier = Modifier.weight(1f))
        }
        Row2Preview {
            Button("Ghost",       onClick = {}, variant = ButtonVariant.Ghost,       modifier = Modifier.weight(1f))
            Button("Link",        onClick = {}, variant = ButtonVariant.Link,        modifier = Modifier.weight(1f))
        }

        SectionLabel("Sizes")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button("Sm",      onClick = {}, size = ButtonSize.Sm)
            Button("Default", onClick = {})
            Button("Lg",      onClick = {}, size = ButtonSize.Lg)
        }

        SectionLabel("States")
        Row2Preview {
            Button("Loading",  onClick = {}, isLoading = true, modifier = Modifier.weight(1f))
            Button("Disabled", onClick = {}, enabled   = false, modifier = Modifier.weight(1f))
        }

        SectionLabel("Icon size")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, size = ButtonSize.Icon) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Button(onClick = {}, size = ButtonSize.Icon, variant = ButtonVariant.Outline) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Button(onClick = {}, size = ButtonSize.Icon, variant = ButtonVariant.Destructive) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Label
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Label — light", showBackground = true, widthDp = 360)
@Preview(name = "Label — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLabel() {
    PreviewSurface {
        Label("Default label")
        Label("Disabled label", disabled = true)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Input
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Input — light", showBackground = true, widthDp = 360)
@Preview(name = "Input — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewInput() {
    PreviewSurface {
        SectionLabel("Default")
        Input(value = "", onValueChange = {}, placeholder = "m@example.com")

        SectionLabel("With leading icon")
        Input(
            value        = "",
            onValueChange = {},
            placeholder  = "m@example.com",
            leadingIcon  = { Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp)) }
        )

        SectionLabel("Password")
        Input(value = "secret", onValueChange = {}, isPassword = true)

        SectionLabel("Error")
        Input(value = "bad@", onValueChange = {}, isError = true)

        SectionLabel("Disabled")
        Input(value = "Read only", onValueChange = {}, enabled = false)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. FormField
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "FormField — light", showBackground = true, widthDp = 360)
@Preview(name = "FormField — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFormField() {
    PreviewSurface {
        SectionLabel("Normal")
        FormField(
            label         = "Email address",
            value         = "",
            onValueChange  = {},
            placeholder   = "m@example.com",
            helperText    = "We'll never share your email."
        )

        SectionLabel("Error state")
        FormField(
            label         = "Email address",
            value         = "bad@",
            onValueChange  = {},
            isError       = true,
            errorMessage  = "Please enter a valid email address."
        )

        SectionLabel("Disabled")
        FormField(
            label         = "Username",
            value         = "johndoe",
            onValueChange  = {},
            enabled       = false
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. InputOTP
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "InputOTP — light", showBackground = true, widthDp = 360)
@Preview(name = "InputOTP — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewInputOTP() {
    PreviewSurface {
        SectionLabel("6-digit code (partially filled)")
        InputOTP(value = "421", onValueChange = {}, length = 6)

        SectionLabel("Grouped (3-3)")
        InputOTP(value = "12", onValueChange = {}, length = 6, groups = listOf(3, 3))

        SectionLabel("Complete / success")
        InputOTP(value = "123456", onValueChange = {}, length = 6)

        SectionLabel("Error")
        InputOTP(value = "123", onValueChange = {}, length = 6, isError = true)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Combobox
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Combobox — light", showBackground = true, widthDp = 360)
@Preview(name = "Combobox — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCombobox() {
    val items = listOf(
        ComboboxItem("next",   "Next.js"),
        ComboboxItem("nuxt",   "Nuxt.js"),
        ComboboxItem("svelte", "SvelteKit"),
        ComboboxItem("astro",  "Astro"),
    )
    PreviewSurface {
        SectionLabel("Nothing selected")
        Combobox(items = items, selected = null, onSelect = {}, placeholder = "Select framework…")

        SectionLabel("With selection")
        Combobox(items = items, selected = items[0], onSelect = {}, placeholder = "Select framework…")

        SectionLabel("Disabled")
        Combobox(items = items, selected = null, onSelect = {}, placeholder = "Select framework…", enabled = false)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  7. DatePicker
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "DatePicker — light", showBackground = true, widthDp = 360)
@Preview(name = "DatePicker — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDatePicker() {
    PreviewSurface {
        SectionLabel("Empty")
        DatePicker(selected = null, onSelect = {})

        SectionLabel("With selection")
        DatePicker(selected = LocalDate.of(2025, 6, 15), onSelect = {})

        SectionLabel("Disabled")
        DatePicker(selected = null, onSelect = {}, enabled = false)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  8. Skeleton
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Skeleton — light", showBackground = true, widthDp = 360)
@Preview(name = "Skeleton — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSkeleton() {
    PreviewSurface {
        SectionLabel("Text lines")
        Skeleton(modifier = Modifier.fillMaxWidth().height(14.dp))
        Skeleton(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))
        Skeleton(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp))

        SectionLabel("List items")
        SkeletonListItem()
        SkeletonListItem()

        SectionLabel("Card")
        SkeletonCard(imageHeight = 110.dp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  9. Spinner
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Spinner — light", showBackground = true, widthDp = 360)
@Preview(name = "Spinner — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSpinner() {
    PreviewSurface {
        SectionLabel("All sizes")
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Spinner(size = SpinnerSize.Sm)
            Spinner(size = SpinnerSize.Default)
            Spinner(size = SpinnerSize.Lg)
            Spinner(size = SpinnerSize.Xl)
        }

        SectionLabel("With label")
        Spinner(size = SpinnerSize.Default, label = "Loading data…")

        SectionLabel("Custom color")
        Spinner(size = SpinnerSize.Default, color = MaterialTheme.colorScheme.error, label = "Error color")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  10. Pagination
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Pagination — light", showBackground = true, widthDp = 360)
@Preview(name = "Pagination — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPagination() {
    PreviewSurface {
        SectionLabel("Page 1 of 5")
        Pagination(currentPage = 1, totalPages = 5, onPageChange = {})

        SectionLabel("Page 5 of 20 (with ellipsis)")
        Pagination(currentPage = 5, totalPages = 20, onPageChange = {})

        SectionLabel("Last page")
        Pagination(currentPage = 20, totalPages = 20, onPageChange = {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  11. Stepper
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Stepper — light", showBackground = true, widthDp = 360)
@Preview(name = "Stepper — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewStepper() {
    val steps = listOf(
        Step("Account", "Create account"),
        Step("Profile", "Tell us about you"),
        Step("Review",  "Confirm details"),
    )
    PreviewSurface {
        SectionLabel("Horizontal — step 0")
        Stepper(steps = steps, currentStep = 0, orientation = StepperOrientation.Horizontal)

        SectionLabel("Horizontal — step 1 (mid)")
        Stepper(steps = steps, currentStep = 1, orientation = StepperOrientation.Horizontal)

        SectionLabel("Horizontal — step 2 (last)")
        Stepper(steps = steps, currentStep = 2, orientation = StepperOrientation.Horizontal)

        SectionLabel("Vertical — step 1")
        Stepper(steps = steps, currentStep = 1, orientation = StepperOrientation.Vertical)

        SectionLabel("With error state")
        Stepper(
            steps = listOf(
                Step("Account", state = StepState.Completed),
                Step("Profile", state = StepState.Error),
                Step("Review",  state = StepState.Upcoming),
            ),
            currentStep = 1
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  12. Carousel
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Carousel — light", showBackground = true, widthDp = 360)
@Preview(name = "Carousel — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCarousel() {
    PreviewSurface {
        Carousel(pageCount = 3) { page ->
            val colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.tertiaryContainer,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors[page]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Slide ${page + 1}",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  13. DataTable
// ─────────────────────────────────────────────────────────────────────────────

private data class PreviewPayment(val id: String, val status: String, val method: String, val amount: String)

@Preview(name = "DataTable — light", showBackground = true, widthDp = 360)
@Preview(name = "DataTable — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDataTable() {
    val data = listOf(
        PreviewPayment("INV001", "Paid",    "Credit Card",   "$250.00"),
        PreviewPayment("INV002", "Pending", "PayPal",        "$150.00"),
        PreviewPayment("INV003", "Failed",  "Bank Transfer", "$350.00"),
    )

    @Composable
    fun statusColor(s: String) = when (s) {
        "Paid"    -> MaterialTheme.colorScheme.primary
        "Pending" -> MaterialTheme.colorScheme.secondary
        else      -> MaterialTheme.colorScheme.error
    }

    val columns = listOf(
        TableColumn<PreviewPayment>("id",     "Invoice", weight = 1.2f) {
            Text(it.id, fontSize = 12.sp) },
        TableColumn<PreviewPayment>("status", "Status", sortable = true) {
            Text(it.status, fontSize = 12.sp, color = statusColor(it.status), fontWeight = FontWeight.Medium) },
        TableColumn<PreviewPayment>("method", "Method", weight = 1.3f) {
            Text(it.method, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        TableColumn<PreviewPayment>("amount", "Amount", sortable = true) {
            Text(it.amount, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
    )

    PreviewSurface {
        SectionLabel("Default")
        DataTable(columns = columns, data = data)

        SectionLabel("Striped")
        DataTable(columns = columns, data = data, striped = true)

        SectionLabel("Empty state")
        DataTable(columns = columns, data = emptyList())
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  14. Empty
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Empty — light", showBackground = true, widthDp = 360)
@Preview(name = "Empty — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewEmpty() {
    PreviewSurface {
        SectionLabel("Default (icon variant)")
        Empty {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                EmptyTitle("No Projects Yet")
                EmptyDescription("You haven't created any projects yet. Get started by creating your first project.")
            }
            EmptyContent {
                Button(text = "Create Project", onClick = {})
                Button(text = "Import Project",  onClick = {}, variant = ButtonVariant.Outline)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        SectionLabel("Outline border")
        Empty(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon,
                    iconBoxColor = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Default.Cloud, contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
                EmptyTitle("Cloud Storage Empty")
                EmptyDescription("Upload files to your cloud storage to access them anywhere.")
            }
            EmptyContent { Button(text = "Upload Files", onClick = {}) }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        SectionLabel("Background")
        Empty(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon) {
                    Icon(Icons.Default.Notifications, contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                EmptyTitle("No Notifications")
                EmptyDescription("You're all caught up. New notifications will appear here.")
            }
            EmptyContent { Button(text = "Refresh", onClick = {}, variant = ButtonVariant.Outline) }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        SectionLabel("Avatar variant")
        Empty {
            EmptyHeader {
                EmptyMedia {
                    EmptyAvatar(
                        initials       = "LR",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                EmptyTitle("User Offline")
                EmptyDescription("This user is currently offline. Leave a message or try again later.")
            }
            EmptyContent { Button(text = "Leave Message", onClick = {}) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  15. Direction
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Direction — LTR light", showBackground = true, widthDp = 360)
@Preview(name = "Direction — LTR dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDirectionLtr() {
    PreviewSurface {
        DirectionProvider(direction = LayoutDirection.Ltr) {
            SectionLabel("LTR — left to right")
            DirectionDemoCard(isRtl = false)
        }
    }
}

@Preview(name = "Direction — RTL light", showBackground = true, widthDp = 360)
@Preview(name = "Direction — RTL dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDirectionRtl() {
    PreviewSurface {
        DirectionProvider(direction = LayoutDirection.Rtl) {
            SectionLabel("RTL — right to left")
            DirectionDemoCard(isRtl = true)
        }
    }
}

@Composable
private fun DirectionDemoCard(isRtl: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        color    = MaterialTheme.colorScheme.surface,
        border   = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text      = if (isRtl) "تسجيل الدخول إلى حسابك" else "Sign in to your account",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onBackground,
                textAlign  = if (isRtl) TextAlign.End else TextAlign.Start,
                modifier   = Modifier.fillMaxWidth()
            )
            FormField(
                label         = if (isRtl) "البريد الإلكتروني" else "Email",
                value         = "",
                onValueChange  = {},
                placeholder   = "m@example.com"
            )
            FormField(
                label         = if (isRtl) "كلمة المرور" else "Password",
                value         = "",
                onValueChange  = {},
                isPassword    = true
            )
            Button(
                text     = if (isRtl) "تسجيل الدخول" else "Sign in",
                onClick  = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  16. Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "AlertDialog — light", showBackground = true, widthDp = 360)
@Preview(name = "AlertDialog — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewAlertDialog() {
    // AlertDialog renders as a standalone card so we preview it inline
    PreviewSurface {
        SectionLabel("Destructive alert dialog (inline preview)")
        Surface(
            shape  = RoundedCornerShape(12.dp),
            color  = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier            = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Are you absolutely sure?",
                    fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "This action cannot be undone. This will permanently delete your account.",
                    fontSize = 14.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button("Cancel",         onClick = {}, variant = ButtonVariant.Outline,     size = ButtonSize.Sm)
                    Button("Delete account", onClick = {}, variant = ButtonVariant.Destructive, size = ButtonSize.Sm)
                }
            }
        }

        SectionLabel("Free-form dialog (inline preview)")
        Surface(
            shape    = RoundedCornerShape(12.dp),
            color    = MaterialTheme.colorScheme.surface,
            border   = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier            = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DialogHeader(title = "Edit profile", description = "Make changes here. Click save when done.")
                FormField(label = "Name", value = "John Doe", onValueChange = {})
                DialogFooter {
                    Button("Cancel",       onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm)
                    Button("Save changes", onClick = {}, size = ButtonSize.Sm)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  17. Toaster
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Toaster — light", showBackground = true, widthDp = 360)
@Preview(name = "Toaster — dark",  showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewToaster() {
    // Toast cards rendered statically — no animation needed in preview
    PreviewSurface {
        SectionLabel("Trigger buttons")
        Row2Preview {
            Button("Default", onClick = {}, variant = ButtonVariant.Outline,     size = ButtonSize.Sm, modifier = Modifier.weight(1f))
            Button("Success", onClick = {}, size = ButtonSize.Sm, modifier = Modifier.weight(1f))
        }
        Row2Preview {
            Button("Error",   onClick = {}, variant = ButtonVariant.Destructive, size = ButtonSize.Sm, modifier = Modifier.weight(1f))
            Button("Warning", onClick = {}, variant = ButtonVariant.Secondary,   size = ButtonSize.Sm, modifier = Modifier.weight(1f))
        }

        SectionLabel("Toast cards (static preview)")
        // Default
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(10.dp),
            color    = MaterialTheme.colorScheme.surface,
            border   = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface)
                Text("Event has been created.", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }
        // Success
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color = androidx.compose.ui.graphics.Color(0xFF166534)) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp),
                    tint = androidx.compose.ui.graphics.Color(0xFF4ADE80))
                Column {
                    Text("Saved!", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = androidx.compose.ui.graphics.Color(0xFFDCFCE7))
                    Text("Your changes have been saved.", fontSize = 11.sp,
                        color = androidx.compose.ui.graphics.Color(0xFFDCFCE7).copy(alpha = 0.8f))
                }
            }
        }
        // Error
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.errorContainer) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error)
                Column {
                    Text("Uh oh!", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer)
                    Text("Something went wrong.", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                }
            }
        }
        // With action
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Email sent.", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                TextButton(onClick = {}) {
                    Text("Undo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  18. Full showcase screen
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Showcase screen — light", showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun PreviewShowcaseScreenLight() {
    AppTheme {
        Surface(
            color    = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Component Showcase", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground)
                    Text("All shadcn/ui components — Kotlin/Compose",
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Show a condensed cross-section so the preview renders quickly
                PreviewButtons()
                PreviewInputOTP()
                PreviewEmpty()
            }
        }
    }
}
