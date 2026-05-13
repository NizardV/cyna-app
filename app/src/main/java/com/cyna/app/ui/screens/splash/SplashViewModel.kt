package com.cyna.app.ui.screens.splash

import android.app.Application
import com.cyna.app.ui.core.Destination
import com.cyna.app.ui.core.ViewModel
import kotlinx.coroutines.delay

interface SplashContracts {
    data class UiState(
        val isLoading: Boolean = true
    )

    sealed interface UiAction {
        object SplashCompleted : UiAction
    }

    sealed interface Event {
        object NavigateTo : Event
    }
}

class SplashViewModel(
    application: Application
) : ViewModel<SplashContracts.UiState>(
    initialState = SplashContracts.UiState(),
    application = application
) {

    init {
        // Start the splash timer when ViewModel is created
        startSplashTimer()
    }

    fun handleAction(action: SplashContracts.UiAction) {
        when (action) {
            SplashContracts.UiAction.SplashCompleted -> navigateTo()
        }
    }

    private fun startSplashTimer() {
        fetchData(
            source = {
                delay(2000) // 2 seconds splash duration
                true // Return a dummy value since we just need the delay
            },
            onResult = {
                onSuccess {
                    sendEvent(Destination.Login)
                }
                onFailure {
                    // Even if there's an error, navigate to login after delay
                    sendEvent(Destination.Login)
                }
            }
        )
    }

    private fun navigateTo() {
        sendEvent(Destination.Login)
    }
}