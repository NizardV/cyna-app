package com.cyna.app.ui.core.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SearchBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String = "Rechercher...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
            )
        },
        placeholder = {
            Text(
                placeholder,
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}

// Special composable for showing focused state in previews
@Composable
fun SearchBarFocusedPreview(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String = "Rechercher...",
    modifier: Modifier = Modifier
) {
    // Create a custom interaction source to simulate focused state
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
            )
        },
        placeholder = {
            Text(
                placeholder,
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        interactionSource = interactionSource
    )
}