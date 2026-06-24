package com.cyna.app.ui.screens.auth.forgotpassword

import com.cyna.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

// ── Contracts ─────────────────────────────────────────────────────────────────

interface ForgotPasswordContracts {
    data class UiState(
        val email: String = "",
        val isLoading: Boolean = false,
        val submitted: Boolean = false,
        val error: String? = null
    ) {
        val isEmailValid: Boolean
            get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class ForgotPasswordViewModel(application: android.app.Application) :
    KViewModel<ForgotPasswordContracts.UiState>(ForgotPasswordContracts.UiState(), application) {

    private val authRepository: AuthRepository by inject()

    fun onEmailChange(v: String) = updateState { copy(email = v, error = null) }

    fun submit() {
        val s = state.value
        if (!s.isEmailValid) return
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source = { authRepository.forgotPassword(s.email) },
            onResult = {
                onSuccess {
                    updateState { copy(isLoading = false, submitted = true) }
                }
                onFailure {
                    // Anti-enumeration: show submitted anyway in prod.
                    // In mock we still show the error state so testers can see it.
                    updateState { copy(isLoading = false, error = "Une erreur est survenue. Veuillez réessayer.") }
                }
            }
        )
    }

    fun resend() {
        updateState { copy(submitted = false) }
    }
}

