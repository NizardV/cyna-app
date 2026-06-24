package com.cyna.app.ui.screens.auth.resetpassword

import com.cyna.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

// ── Password rules (mirrors web PWD_RULES) ────────────────────────────────────

data class PwdRule(
    val id: String,
    val label: String,
    val test: (String) -> Boolean
)

private val PWD_RULES = listOf(
    PwdRule("length",    "8 caractères minimum")    { it.length >= 8 },
    PwdRule("uppercase", "Une majuscule")           { it.any(Char::isUpperCase) },
    PwdRule("number",    "Un chiffre")              { it.any(Char::isDigit) },
    PwdRule("special",   "Un caractère spécial")   { p -> p.any { !it.isLetterOrDigit() } }
)

// ── Contracts ─────────────────────────────────────────────────────────────────

interface ResetPasswordContracts {
    data class UiState(
        val email: String = "",
        val code: String = "",
        val password: String = "",
        val showRules: Boolean = false,
        val isLoading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null
    ) {
        val rules get() = PWD_RULES.map { r -> r to r.test(password) }
        val isPwdValid get() = rules.all { (_, ok) -> ok }
        val isEmailValid get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isReady get() = isEmailValid && code.trim().length == 6 && isPwdValid
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ResetPasswordViewModel(application: android.app.Application) :
    KViewModel<ResetPasswordContracts.UiState>(ResetPasswordContracts.UiState(), application) {

    private val authRepository: AuthRepository by inject()

    fun initEmail(email: String) {
        if (state.value.email.isBlank() && email.isNotBlank()) {
            updateState { copy(email = email) }
        }
    }

    fun onEmailChange(v: String) = updateState { copy(email = v, error = null) }
    fun onCodeChange(v: String)  = updateState { copy(code = v,  error = null) }
    fun onPasswordChange(v: String) = updateState { copy(password = v, error = null) }
    fun onPasswordFocus() = updateState { copy(showRules = true) }

    fun submit() {
        val s = state.value
        if (!s.isReady) return
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source = { authRepository.resetPassword(s.email, s.code.trim(), s.password) },
            onResult = {
                onSuccess { updateState { copy(isLoading = false, success = true) } }
                onFailure { e ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = e.message ?: "Code invalide ou expiré. Vérifiez le code reçu par email."
                        )
                    }
                }
            }
        )
    }
}
