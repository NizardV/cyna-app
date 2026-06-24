package com.cyna.app.ui.screens.auth.security2fa

import com.cyna.app.domain.model.TwoFactorSetup
import com.cyna.app.domain.repository.TwoFactorRepository
import dev.kindling.compose.KViewModel
import dev.kindling.core.components.KToastManager
import org.koin.core.component.inject

// ── Contracts ─────────────────────────────────────────────────────────────────

interface Security2FAContracts {
    data class UiState(
        val setup: TwoFactorSetup? = null,
        val loadingSetup: Boolean = true,
        val setupError: String? = null,
        val code: String = "",
        val confirming: Boolean = false,
        val confirmError: String? = null,
        val activated: Boolean = false
    )
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class Security2FAViewModel(application: android.app.Application) :
    KViewModel<Security2FAContracts.UiState>(Security2FAContracts.UiState(), application) {

    private val twoFactorRepository: TwoFactorRepository by inject()

    init { loadSetup() }

    private fun loadSetup() {
        fetchData(
            source = { twoFactorRepository.setup() },
            onResult = {
                onSuccess { setup ->
                    updateState { copy(setup = setup, loadingSetup = false) }
                }
                onFailure { e ->
                    updateState {
                        copy(
                            loadingSetup = false,
                            setupError = e.message ?: "Impossible de générer la clé 2FA. Réessayez plus tard."
                        )
                    }
                }
            }
        )
    }

    fun onCodeChange(v: String) = updateState { copy(code = v, confirmError = null) }

    fun confirm() {
        val s = state.value
        if (s.code.trim().length != 6) return
        updateState { copy(confirming = true, confirmError = null) }
        fetchData(
            source = { twoFactorRepository.confirm(s.code.trim()) },
            onResult = {
                onSuccess {
                    updateState { copy(confirming = false, activated = true) }
                    KToastManager.success("Authentification à deux facteurs activée.")
                }
                onFailure {
                    updateState {
                        copy(
                            confirming = false,
                            confirmError = "Code invalide. Vérifiez l'heure de votre téléphone et réessayez.",
                            code = ""
                        )
                    }
                }
            }
        )
    }
}
