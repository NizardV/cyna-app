package com.cyna.app.ui.core.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Variantes de couleur pour le bouton primaire
 */
enum class ButtonVariant {
    PRIMARY,    // Couleur primaire par défaut
    SECONDARY,  // Couleur secondaire
    SUCCESS,    // Couleur de succès
    ERROR       // Couleur d'erreur
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fontWeight: FontWeight = FontWeight.Medium,
    variant: ButtonVariant = ButtonVariant.PRIMARY
) {
    val (containerColor, contentColor, disabledContainerColor, disabledContentColor) = when (variant) {
        ButtonVariant.PRIMARY -> {
            Quadruple(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.onPrimary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.onTertiary
            )
        }
        ButtonVariant.SECONDARY -> {
            Quadruple(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.onSecondary,
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
            )
        }
        ButtonVariant.SUCCESS -> {
            Quadruple(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onPrimaryContainer,
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
            )
        }
        ButtonVariant.ERROR -> {
            Quadruple(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.onError,
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
            )
        }
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        ),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = contentColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(text, fontWeight = fontWeight)
        }
    }
}

// Classe utilitaire pour retourner 4 valeurs
private data class Quadruple<T>(
    val first: T,
    val second: T,
    val third: T,
    val fourth: T
)