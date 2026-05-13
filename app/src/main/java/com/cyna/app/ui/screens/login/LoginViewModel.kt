package com.cyna.app.ui.screens.login

import android.app.Application
import com.cyna.app.domain.repository.LoginRepository
import com.cyna.app.ui.core.Destination
import com.cyna.app.ui.core.ViewModel
import org.koin.core.component.inject

interface LoginContracts {
    data class UiState(
        val identifier: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isButtonEnabled: Boolean = false
    )

    sealed interface UiAction {
        data class IdentifierChanged(val identifier: String) : UiAction
        object LoginClicked : UiAction
    }

    sealed interface Event {
        object NavigateToHome : Event
        data class ShowError(val message: String) : Event
    }
}

class LoginViewModel(
    application: Application
) : ViewModel<LoginContracts.UiState>(
    initialState = LoginContracts.UiState(),
    application = application
) {
    private val loginService: LoginRepository by inject()

    fun handleAction(action: LoginContracts.UiAction) {
        when (action) {
            is LoginContracts.UiAction.IdentifierChanged -> updateIdentifier(action.identifier)
            LoginContracts.UiAction.LoginClicked -> login()
        }
    }

    private fun updateIdentifier(identifier: String) {
        updateState {
            copy(
                identifier = identifier,
                errorMessage = null,
                isButtonEnabled = validateIdentifier(identifier) && !isLoading
            )
        }
    }

    private fun login() {
        if (state.value.isLoading) return

        updateState { copy(isLoading = true, errorMessage = null) }

        // First validate locally
        val validationError = getErrorMessage(state.value.identifier)
        if (validationError != null) {
            updateState {
                copy(
                    isLoading = false,
                    errorMessage = validationError,
                    isButtonEnabled = validateIdentifier(state.value.identifier)
                )
            }
            return
        }

        // Call API using fetchData
        fetchData(
            source = { loginService.login(state.value.identifier) },
            onResult = {
                onSuccess { response ->
                    updateState {
                        copy(
                            isLoading = false,
                            isButtonEnabled = validateIdentifier(state.value.identifier)
                        )
                    }
                    sendEvent(Destination.Home)
                }
                onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur de connexion",
                            isButtonEnabled = validateIdentifier(state.value.identifier)
                        )
                    }
                }
            }
        )
    }

    private fun validateIdentifier(identification: String): Boolean {
        return identification.isNotBlank() &&
                identification.length >= 3 &&
                identification.matches(Regex("^[a-zA-Z0-9_]+$"))
    }

    private fun getErrorMessage(identifier: String): String? {
        return when {
            identifier.isBlank() -> "L'identifiant ne peut pas être vide"
            identifier.length < 3 -> "L'identifiant doit contenir au moins 3 caractères"
            !identifier.matches(Regex("^[a-zA-Z0-9_]+$")) -> "L'identifiant ne peut contenir que des lettres, chiffres et underscores"
            else -> null
        }
    }
}