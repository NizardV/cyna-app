package com.cyna.app.ui.screens.profile

import android.app.Application
import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User
import com.cyna.app.domain.repository.UserRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

interface ProfileContracts {
    data class UiState(
        val user: User? = null,
        val subscriptions: List<Subscription> = emptyList(),
        val loadingUser: Boolean = true,
        val loadingSubs: Boolean = true,
        val nameInput: String = "",
        val emailInput: String = "",
        val savingProfile: Boolean = false,
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val savingPassword: Boolean = false,
        val cancelTarget: Subscription? = null,
        val cancelling: Boolean = false
    )

    sealed interface Event {
        data class Toast(val message: String, val isError: Boolean = false) : Event
        object LoggedOut : Event
    }
}

class ProfileViewModel(application: Application) :
    KViewModel<ProfileContracts.UiState>(ProfileContracts.UiState(), application) {

    private val userRepository: UserRepository by inject()

    init {
        loadUser()
        loadSubscriptions()
    }

    private fun loadUser() {
        fetchData(
            source = { userRepository.getMe() },
            onResult = {
                onSuccess { user ->
                    updateState {
                        copy(
                            user        = user,
                            nameInput   = user.name,
                            emailInput  = user.email,
                            loadingUser = false
                        )
                    }
                }
                onFailure {
                    updateState { copy(loadingUser = false) }
                }
            }
        )
    }

    private fun loadSubscriptions() {
        fetchData(
            source = { userRepository.getSubscriptions() },
            onResult = {
                onSuccess { subs ->
                    updateState { copy(subscriptions = subs, loadingSubs = false) }
                }
                onFailure {
                    updateState { copy(loadingSubs = false) }
                }
            }
        )
    }

    fun onNameChange(v: String)            = updateState { copy(nameInput = v) }
    fun onEmailChange(v: String)           = updateState { copy(emailInput = v) }
    fun onCurrentPasswordChange(v: String) = updateState { copy(currentPassword = v) }
    fun onNewPasswordChange(v: String)     = updateState { copy(newPassword = v) }
    fun onConfirmPasswordChange(v: String) = updateState { copy(confirmPassword = v) }

    fun saveProfile() {
        val s = state.value
        updateState { copy(savingProfile = true) }
        fetchData(
            source = { userRepository.updateProfile(s.nameInput, s.emailInput) },
            onResult = {
                onSuccess { user ->
                    updateState { copy(savingProfile = false, user = user) }
                    sendEvent(ProfileContracts.Event.Toast("Profile updated successfully"))
                }
                onFailure { e ->
                    updateState { copy(savingProfile = false) }
                    sendEvent(ProfileContracts.Event.Toast(e.message ?: "An error occurred", isError = true))
                }
            }
        )
    }

    fun savePassword() {
        val s = state.value
        if (s.newPassword != s.confirmPassword) {
            sendEvent(ProfileContracts.Event.Toast("Passwords do not match", isError = true))
            return
        }
        if (s.newPassword.length < 8) {
            sendEvent(ProfileContracts.Event.Toast("Password must be at least 8 characters", isError = true))
            return
        }
        updateState { copy(savingPassword = true) }
        fetchData(
            source = { userRepository.updatePassword(s.currentPassword, s.newPassword) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            savingPassword  = false,
                            currentPassword = "",
                            newPassword     = "",
                            confirmPassword = ""
                        )
                    }
                    sendEvent(ProfileContracts.Event.Toast("Password updated successfully"))
                }
                onFailure { e ->
                    updateState { copy(savingPassword = false) }
                    sendEvent(ProfileContracts.Event.Toast(e.message ?: "An error occurred", isError = true))
                }
            }
        )
    }

    fun requestCancel(sub: Subscription) = updateState { copy(cancelTarget = sub) }
    fun dismissCancel()                  = updateState { copy(cancelTarget = null) }

    fun confirmCancel() {
        val target = state.value.cancelTarget ?: return
        updateState { copy(cancelling = true) }
        fetchData(
            source = { userRepository.cancelSubscription(target.id) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            cancelling    = false,
                            cancelTarget  = null,
                            subscriptions = subscriptions.filter { it.id != target.id }
                        )
                    }
                    sendEvent(ProfileContracts.Event.Toast("Subscription cancelled"))
                }
                onFailure { e ->
                    updateState { copy(cancelling = false, cancelTarget = null) }
                    sendEvent(ProfileContracts.Event.Toast(e.message ?: "An error occurred", isError = true))
                }
            }
        )
    }
}