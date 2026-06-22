package com.cyna.app.ui.screens.auth.confirmemail

import com.cyna.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject


// ── Contracts ─────────────────────────────────────────────────────────────────

interface ConfirmEmailContracts {
    data class UiState(
        val email: String = "",
        val code: String = "",
        val isLoading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null
    ) {
        val isEmailValid: Boolean
            get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isReady: Boolean
            get() = isEmailValid && code.trim().length == 6
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ConfirmEmailViewModel(application: android.app.Application) :
    KViewModel<ConfirmEmailContracts.UiState>(ConfirmEmailContracts.UiState(), application) {

    private val authRepository: AuthRepository by inject()

    fun initEmail(email: String) {
        if (state.value.email.isBlank() && email.isNotBlank()) {
            updateState { copy(email = email) }
        }
    }

    fun onEmailChange(v: String) = updateState { copy(email = v, error = null) }
    fun onCodeChange(v: String)  = updateState { copy(code = v, error = null) }

    fun submit() {
        val s = state.value
        if (!s.isReady) return
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source = { authRepository.confirmEmail(s.email, s.code.trim()) },
            onResult = {
                onSuccess { updateState { copy(isLoading = false, success = true) } }
                onFailure { e ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = e.message ?: "Code invalide ou expiré. Vérifiez votre email ou demandez un nouveau code."
                        )
                    }
                }
            }
        )
    }

    fun resend() {
        val email = state.value.email
        if (!state.value.isEmailValid) return
        // Reuse forgot-password to trigger a new verification code
        fetchData(
            source = { authRepository.forgotPassword(email) },
            onResult = {
                onSuccess { /* silent success */ }
                onFailure { e ->
                    updateState { copy(error = "Impossible de renvoyer le code. Réessayez dans quelques instants.") }
                }
            }
        )
    }
}
