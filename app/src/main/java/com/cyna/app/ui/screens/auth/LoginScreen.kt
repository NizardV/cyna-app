package com.cyna.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
fun LoginScreen(
    navController: NavController,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: () -> Unit,
) {
    KScreen(
        viewModel = viewModel<AuthViewModel>(),
        navController = navController
    ) { state, vm ->
        LoginContent(
            state = state,
            onEmailChange = vm::onEmailChange,
            onPasswordChange = vm::onPasswordChange,
            onLogin = { vm.login(onLoginSuccess) },
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    }
}

@Composable
private fun LoginContent(
    state: AuthContracts.UiState = AuthContracts.UiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var rememberMe by remember { mutableStateOf(false) }

    // Full-screen purple-tinted background matching #f4f4f6
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
            // White card — rounded-2xl border border-gray-100 bg-white p-8 shadow-sm
            AuthCard {

                // Lock icon in purple rounded square
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEDE9FE)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Title
                Text(
                    text = stringResource(R.string.login_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle with register link
                val subtitle = buildAnnotatedString {
                    append(stringResource(R.string.login_subtitle_prefix))
                    withStyle(SpanStyle(color = Color(0xFF7C3AED), fontWeight = FontWeight.SemiBold)) {
                        append(stringResource(R.string.login_subtitle_link))
                    }
                }
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(subtitle, fontSize = 14.sp, color = Color(0xFF6B7280))
                }

                Spacer(Modifier.height(24.dp))

                // Error banner
                AnimatedVisibility(visible = state.emailError != null || state.passwordError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.emailError ?: state.passwordError ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Email
                FieldWithLabel(
                    label = stringResource(R.string.login_email_label),
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = stringResource(R.string.login_email_placeholder),
                    isError = state.emailError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.emailError != null) {
                    Text(
                        text = state.emailError,
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Password
                FieldWithLabel(
                    label = stringResource(R.string.login_password_label),
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = stringResource(R.string.login_password_placeholder),
                    isPassword = true,
                    isError = state.passwordError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Remember me + Forgot password row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            enabled = !state.isLoading,
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF7C3AED),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = stringResource(R.string.login_remember_me),
                            fontSize = 13.sp,
                            color = Color(0xFF374151)
                        )
                    }
                    TextButton(
                        onClick = onNavigateToForgotPassword,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.login_forgot_password),
                            fontSize = 13.sp,
                            color = Color(0xFF7C3AED),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Submit button — dark background like web (bg-gray-900)
                KButton(
                    text = stringResource(R.string.login_button),
                    onClick = onLogin,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}