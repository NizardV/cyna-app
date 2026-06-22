package com.cyna.app.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cyna.app.data.local.SessionManager
import com.cyna.app.ui.core.components.ui.AccountSection
import com.cyna.app.ui.core.components.ui.NavTab
import com.cyna.app.ui.screens.auth.LoginScreen
import com.cyna.app.ui.screens.auth.RegisterScreen
import com.cyna.app.ui.screens.auth.confirmemail.ConfirmEmailScreen
import com.cyna.app.ui.screens.auth.forgotpassword.ForgotPasswordScreen
import com.cyna.app.ui.screens.auth.resetpassword.ResetPasswordScreen
import com.cyna.app.ui.screens.auth.security2fa.Security2FAScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.koinInject
import java.net.URLDecoder
import java.net.URLEncoder

// ── Destinations ──────────────────────────────────────────────────────────────

@Serializable
sealed class Destination {
    abstract val route: String

    @Transient
    open val arguments: List<NamedNavArgument> = emptyList()

    @Serializable
    object Login : Destination() {
        override val route = "login"
    }

    @Serializable
    object Register : Destination() {
        override val route = "register"
    }

    @Serializable
    object ForgotPassword : Destination() {
        override val route = "forgot-password"
    }

    @Serializable
    object ResetPassword : Destination() {
        override val route = "reset-password?email={email}"
        override val arguments = listOf(
            navArgument("email") {
                type         = NavType.StringType
                defaultValue = ""
            }
        )

        fun withEmail(email: String) =
            "reset-password?email=${URLEncoder.encode(email, "UTF-8")}"
    }

    @Serializable
    object ConfirmEmail : Destination() {
        override val route = "confirm-email?email={email}"
        override val arguments = listOf(
            navArgument("email") {
                type         = NavType.StringType
                defaultValue = ""
            }
        )

        fun withEmail(email: String) =
            "confirm-email?email=${URLEncoder.encode(email, "UTF-8")}"
    }

    @Serializable
    object Security2FA : Destination() {
        override val route = "account/security/2fa"
    }

    @Serializable
    object Profile : Destination() {
        override val route = "profile"
    }

    @Serializable
    object OrdersHistory : Destination() {
        override val route = "orders-history"
    }
}

// ── NavGraphBuilder helpers ───────────────────────────────────────────────────

fun NavGraphBuilder.composable(
    destination: Destination,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) = composable(
    route     = destination.route,
    arguments = destination.arguments,
    deepLinks = deepLinks
) { backStackEntry -> content(backStackEntry) }

// ── NavController helpers ─────────────────────────────────────────────────────

fun NavController.navigateTo(
    destination: Destination,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) = navigate(route = destination.route, navOptions = navOptions, navigatorExtras = navigatorExtras)

fun NavController.navigateTo(
    destination: Destination,
    builder: NavOptionsBuilder.() -> Unit
) = navigate(route = destination.route, builder = builder)

/** Navigate to a destination using its pre-built route string (for parametric routes). */
fun NavController.navigateToRoute(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) = navigate(route = route, builder = builder)

// ── NavHost ───────────────────────────────────────────────────────────────────

@Composable
fun NavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val sessionManager: SessionManager = koinInject()
    val token          by sessionManager.token.collectAsState()
    val isAuthenticated = token != null

    androidx.navigation.compose.NavHost(
        navController    = navController,
        startDestination = if (isAuthenticated) Destination.OrdersHistory.route
        else Destination.Login.route,
        modifier         = modifier
    ) {

        // ── Login ─────────────────────────────────────────────────────────────
        composable(Destination.Login) {
            LoginScreen(
                navController              = navController,
                onNavigateToRegister       = { navController.navigateTo(Destination.Register) },
                onNavigateToForgotPassword = { navController.navigateTo(Destination.ForgotPassword) },
                onLoginSuccess             = {
                    navController.navigateTo(Destination.OrdersHistory) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ──────────────────────────────────────────────────────────
        composable(Destination.Register) {
            RegisterScreen(
                navController     = navController,
                onNavigateToLogin = { navController.navigateTo(Destination.Login) },
                onRegisterSuccess = { email ->
                    // After registration → confirm email (email pre-filled)
                    navController.navigateToRoute(Destination.ConfirmEmail.withEmail(email)) {
                        popUpTo(Destination.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Forgot password ───────────────────────────────────────────────────
        composable(Destination.ForgotPassword) {
            ForgotPasswordScreen(
                navController             = navController,
                onNavigateToResetPassword = { email ->
                    navController.navigateToRoute(Destination.ResetPassword.withEmail(email))
                },
                onNavigateToLogin         = { navController.popBackStack() }
            )
        }

        // ── Reset password ────────────────────────────────────────────────────
        composable(Destination.ResetPassword) { backStack ->
            val email = URLDecoder.decode(
                backStack.arguments?.getString("email") ?: "", "UTF-8"
            )
            ResetPasswordScreen(
                navController     = navController,
                initialEmail      = email,
                onNavigateToLogin = {
                    navController.navigateTo(Destination.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Confirm email ─────────────────────────────────────────────────────
        composable(Destination.ConfirmEmail) { backStack ->
            val email = URLDecoder.decode(
                backStack.arguments?.getString("email") ?: "", "UTF-8"
            )
            ConfirmEmailScreen(
                navController      = navController,
                initialEmail       = email,
                onNavigateToLogin  = {
                    navController.navigateTo(Destination.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Security 2FA ──────────────────────────────────────────────────────
        composable(Destination.Security2FA) {
            Security2FAScreen(
                navController       = navController,
                onNavigateToAdmin   = { navController.popBackStack() }, // no admin screen on mobile
                onNavigateToProfile = { navController.popBackStack() }
            )
        }

        // ── Account (bottom nav shell) ────────────────────────────────────────
        composable(Destination.OrdersHistory) {
            AccountSection(
                initialTab      = NavTab.ORDERS,
                onNavigateTo2FA = { navController.navigateTo(Destination.Security2FA) }
            )
        }

        composable(Destination.Profile) {
            AccountSection(
                initialTab      = NavTab.PROFILE,
                onNavigateTo2FA = { navController.navigateTo(Destination.Security2FA) }
            )
        }
    }
}