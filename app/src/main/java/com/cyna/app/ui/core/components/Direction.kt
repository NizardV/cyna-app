package com.diiage.template.ui.core.components.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

// ─────────────────────────────────────────────
//  LayoutDirection enum alias
// ─────────────────────────────────────────────

/**
 * Mirrors shadcn/ui's `"ltr" | "rtl"` direction type.
 * Compose already has [LayoutDirection] — we re-use it directly.
 *
 * [LayoutDirection.Ltr] → left-to-right  (English, French, Spanish…)
 * [LayoutDirection.Rtl] → right-to-left  (Arabic, Hebrew, Persian…)
 */
typealias TextDirection = LayoutDirection

// ─────────────────────────────────────────────
//  DirectionProvider
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style `DirectionProvider`.
 *
 * Wraps a subtree with the given [direction], overriding Compose's
 * [LocalLayoutDirection]. All standard Compose layouts (`Row`, `Column`,
 * `Scaffold`, `TextField` padding, icon positions, etc.) automatically
 * mirror when [LayoutDirection.Rtl] is active.
 *
 * Usage — app-level (in MainActivity / App.kt):
 * ```
 * @Composable
 * fun App() {
 *     val direction = if (isRtlLocale()) LayoutDirection.Rtl else LayoutDirection.Ltr
 *     AppTheme {
 *         DirectionProvider(direction = direction) {
 *             NavHost(rememberNavController())
 *         }
 *     }
 * }
 * ```
 *
 * Usage — screen/component level (e.g. to force RTL for a preview):
 * ```
 * DirectionProvider(direction = LayoutDirection.Rtl) {
 *     LoginScreen(navController)
 * }
 * ```
 *
 * Usage — toggling at runtime:
 * ```
 * var dir by remember { mutableStateOf(DirectionManager.direction) }
 *
 * DirectionProvider(direction = dir) {
 *     Button(onClick = { DirectionManager.toggle(); dir = DirectionManager.direction }) {
 *         Text(if (dir == LayoutDirection.Rtl) "Switch to LTR" else "Switch to RTL")
 *     }
 *     MyContent()
 * }
 * ```
 */
@Composable
fun DirectionProvider(
    direction: LayoutDirection = LayoutDirection.Ltr,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides direction,
        content = content
    )
}

// ─────────────────────────────────────────────
//  useDirection  (hook equivalent)
// ─────────────────────────────────────────────

/**
 * Returns the current [LayoutDirection] from [LocalLayoutDirection].
 *
 * Mirrors shadcn/ui's `useDirection()` hook.
 *
 * ```
 * @Composable
 * fun MyComponent() {
 *     val direction = useDirection()
 *     val isRtl = direction == LayoutDirection.Rtl
 *     Text(if (isRtl) "RTL active" else "LTR active")
 * }
 * ```
 */
@Composable
fun useDirection(): LayoutDirection = LocalLayoutDirection.current

// ─────────────────────────────────────────────
//  DirectionManager  (global singleton for runtime toggling)
// ─────────────────────────────────────────────

/**
 * Singleton that holds the current app-wide direction state.
 *
 * Similar to [ThemeManager], this lets you toggle LTR ↔ RTL from anywhere
 * in the app without threading a parameter down the composable tree.
 *
 * Setup — read the direction at root and pass it to [DirectionProvider]:
 * ```
 * @Composable
 * fun App() {
 *     AppTheme {
 *         DirectionProvider(direction = DirectionManager.direction) {
 *             NavHost(rememberNavController())
 *         }
 *     }
 * }
 * ```
 *
 * Toggle from any screen or settings page:
 * ```
 * Button(onClick = { DirectionManager.toggle() }) {
 *     Text("Toggle RTL / LTR")
 * }
 * ```
 */
object DirectionManager {

    var direction by mutableStateOf<LayoutDirection>(LayoutDirection.Ltr)
        private set

    /** Switch between LTR and RTL. */
    fun toggle() {
        direction = if (direction == LayoutDirection.Ltr) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    /** Explicitly set a direction. */
    fun set(dir: LayoutDirection) {
        direction = dir
    }

    /** Set direction from a locale automatically. */
    fun setFromLocale(locale: java.util.Locale) {
        direction = if (isRtlLocale(locale)) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    val isRtl: Boolean get() = direction == LayoutDirection.Rtl
    val isLtr: Boolean get() = direction == LayoutDirection.Ltr
}

// ─────────────────────────────────────────────
//  Locale helper
// ─────────────────────────────────────────────

/**
 * Returns `true` if [locale] uses right-to-left script.
 *
 * Covers Arabic (ar), Hebrew (he / iw), Persian (fa), Urdu (ur),
 * Pashto (ps), Sindhi (sd), and Kurdish (ku) out of the box.
 */
fun isRtlLocale(locale: java.util.Locale = java.util.Locale.getDefault()): Boolean {
    val rtlLanguages = setOf("ar", "he", "iw", "fa", "ur", "ps", "sd", "ku", "yi", "dv")
    return locale.language.lowercase() in rtlLanguages
}
