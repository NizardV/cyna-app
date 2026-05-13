package com.cyna.app.ui.screens.login

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.ui.core.components.Screen
import com.cyna.app.ui.core.components.input.PrimaryButton
import com.cyna.app.ui.core.components.input.PrimaryTextField
import com.cyna.app.ui.core.components.layout.CenteredBox
import com.cyna.app.ui.core.components.layout.CenteredColumn
import com.cyna.app.ui.core.components.layout.LargeSpacer
import com.cyna.app.ui.core.components.layout.MediumSpacer
import com.cyna.app.ui.core.components.layout.SmallSpacer
import com.cyna.app.ui.core.theme.Yellowcyna

@Composable
fun LoginScreen(navController: NavController) {
    Screen(
        viewModel = viewModel<LoginViewModel>(),
        navController = navController
    ) { state, viewModel ->
        Content(
            state = state,
            handleAction = viewModel::handleAction
        )
    }
}

@Composable
private fun Content(
    state: LoginContracts.UiState = LoginContracts.UiState(),
    handleAction: (LoginContracts.UiAction) -> Unit = { }
) {
    CenteredBox {
        CenteredColumn {
            // App Name
            Text(
                text = "Cyna App",
                color = Yellowcyna,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            LargeSpacer()

            // Title
            Text(
                text = "Créer un compte",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            SmallSpacer()

            // Subtitle
            Text(
                text = "Entrez votre identifiant pour vous inscrire",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LargeSpacer()

            // Error message
            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SmallSpacer()
            }

            // Input Field
            PrimaryTextField(
                value = state.identifier,
                onValueChange = { handleAction(LoginContracts.UiAction.IdentifierChanged(it)) },
                label = "Identifiant",
                enabled = !state.isLoading
            )

            LargeSpacer()

            // Continue Button
            PrimaryButton(
                onClick = { handleAction(LoginContracts.UiAction.LoginClicked) },
                text = if (state.isLoading) "Connexion..." else "Continuer",
                enabled = state.isButtonEnabled && !state.isLoading,
                isLoading = state.isLoading
            )

            MediumSpacer()

            // Disclaimer
            Text(
                text = "En cliquant sur Continuer, vous acceptez nos Conditions d'utilisation et notre Politique de confidentialité.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}