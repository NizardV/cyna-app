package com.cyna.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.R
import com.cyna.app.ui.core.components.ui.AuthCard
import com.cyna.app.ui.core.components.ui.FieldWithLabel
import dev.kindling.compose.KScreen
import dev.kindling.core.components.KButton

@Composable
fun RegisterScreen(
    navController: NavController,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit,   // passes email for confirm-email redirect
) {
    KScreen(
        viewModel = viewModel<AuthViewModel>(),
        navController = navController
    ) { state, vm ->
        RegisterContent(
            state = state,
            onFirstNameChange = vm::onFirstNameChange,
            onLastNameChange = vm::onLastNameChange,
            onEmailChange = vm::onEmailChange,
            onPasswordChange = vm::onPasswordChange,
            onRegister = { vm.register { onRegisterSuccess(state.email) } },
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

@Composable
private fun RegisterContent(
    state: AuthContracts.UiState = AuthContracts.UiState(),
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var showPasswordRules by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFf4f4f6)) {}

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthCard {

                // Title
                Text(
                    text = stringResource(R.string.register_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle with login link
                val subtitle = buildAnnotatedString {
                    append(stringResource(R.string.register_subtitle_prefix))
                    withStyle(SpanStyle(color = Color(0xFF7C3AED), fontWeight = FontWeight.SemiBold)) {
                        append(" ${stringResource(R.string.register_subtitle_link)}")
                    }
                }
                TextButton(
                    onClick = onNavigateToLogin,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(subtitle, fontSize = 14.sp, color = Color(0xFF6B7280))
                }

                Spacer(Modifier.height(24.dp))

                // First name + Last name side by side (matches web grid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldWithLabel(
                            label = stringResource(R.string.register_first_name_label),
                            value = state.firstName,
                            onValueChange = onFirstNameChange,
                            placeholder = stringResource(R.string.register_first_name_placeholder),
                            isError = state.firstNameError != null,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.firstNameError != null) {
                            Text(
                                state.firstNameError,
                                color = Color(0xFFDC2626),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldWithLabel(
                            label = stringResource(R.string.register_last_name_label),
                            value = state.lastName,
                            onValueChange = onLastNameChange,
                            placeholder = stringResource(R.string.register_last_name_placeholder),
                            isError = state.lastNameError != null,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.lastNameError != null) {
                            Text(
                                state.lastNameError,
                                color = Color(0xFFDC2626),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Email
                FieldWithLabel(
                    label = stringResource(R.string.register_email_label),
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = stringResource(R.string.register_email_placeholder),
                    isError = state.emailError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.emailError != null) {
                    Text(
                        state.emailError,
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 3.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Password with focus → show rules
                FieldWithLabel(
                    label = stringResource(R.string.register_password_label),
                    value = state.password,
                    onValueChange = {
                        onPasswordChange(it)
                        if (it.isNotEmpty()) showPasswordRules = true
                    },
                    placeholder = stringResource(R.string.login_password_placeholder),
                    isPassword = true,
                    isError = state.passwordError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password strength rules
                AnimatedVisibility(visible = showPasswordRules && state.password.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PasswordCriterionRow(
                                label = stringResource(R.string.password_criteria_length),
                                met = state.passwordHasMinLength
                            )
                            PasswordCriterionRow(
                                label = stringResource(R.string.password_criteria_uppercase),
                                met = state.passwordHasUppercase
                            )
                            PasswordCriterionRow(
                                label = stringResource(R.string.password_criteria_digit),
                                met = state.passwordHasDigit
                            )
                            PasswordCriterionRow(
                                label = stringResource(R.string.password_criteria_special),
                                met = state.passwordHasSpecial
                            )
                        }
                    }
                }

                // General error
                AnimatedVisibility(visible = state.passwordError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text(
                            state.passwordError ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Submit — purple bg matching web
                KButton(
                    text = stringResource(R.string.register_button),
                    onClick = onRegister,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // "Étape suivante" info box — matches web purple info card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEDE9FE).copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.register_next_step_title),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7C3AED)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.register_next_step_body),
                            fontSize = 13.sp,
                            color = Color(0xFF374151)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordCriterionRow(label: String, met: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (met) Icons.Default.Check else Icons.Default.Circle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (met) Color(0xFF22C55E) else Color(0xFFD1D5DB)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (met) Color(0xFF15803D) else Color(0xFF6B7280)
        )
    }
}