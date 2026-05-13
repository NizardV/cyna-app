package com.cyna.app.ui.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class ThemeState {
    object Light : ThemeState()
    object Dark : ThemeState()
    object System : ThemeState()
}

object ThemeManager {
    var themeState by mutableStateOf<ThemeState>(ThemeState.System)

    fun toggleTheme() {
        themeState = when (themeState) {
            ThemeState.Light -> ThemeState.Dark
            ThemeState.Dark -> ThemeState.System
            ThemeState.System -> ThemeState.Light
        }
    }

    fun setTheme(theme: ThemeState) {
        themeState = theme
    }

    @Composable
    fun isDarkTheme(): Boolean {
        return when (themeState) {
            ThemeState.Light -> false
            ThemeState.Dark -> true
            ThemeState.System -> isSystemInDarkTheme()
        }
    }
}