# Navigation

## Vue d'ensemble

La navigation utilise **Jetpack Navigation Compose**. Toute la configuration est dans `ui/core/Navigation.kt`.

```
MainActivity
    └── App()
            ├── NavHost(navController)        ← navigation principale
            │       ├── LoginScreen
            │       ├── RegisterScreen
            │       ├── ForgotPasswordScreen
            │       ├── ResetPasswordScreen   (paramètre email)
            │       ├── ConfirmEmailScreen    (paramètre email)
            │       ├── Security2FAScreen
            │       ├── OrdersHistory ──→ AccountSection(ORDERS)
            │       └── Profile       ──→ AccountSection(PROFILE)
            │
            └── Toaster()

AccountSection
    └── Scaffold(bottomBar = BottomNavBar)
            └── NavHost(innerNav)
                    ├── orders   → OrderHistoryScreen
                    └── profile  → ProfileScreen ──→ onNavigateTo2FA
```

---

## Destinations

```kotlin
sealed class Destination {

    // ── Auth ─────────────────────────────────────────────────────────────────
    object Login         : Destination() { override val route = "login" }
    object Register      : Destination() { override val route = "register" }
    object ForgotPassword: Destination() { override val route = "forgot-password" }

    // Avec argument email (URL-encoded)
    object ResetPassword : Destination() {
        override val route = "reset-password?email={email}"
        override val arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        fun withEmail(email: String) = "reset-password?email=${URLEncoder.encode(email, "UTF-8")}"
    }

    object ConfirmEmail  : Destination() {
        override val route = "confirm-email?email={email}"
        override val arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        fun withEmail(email: String) = "confirm-email?email=${URLEncoder.encode(email, "UTF-8")}"
    }

    // ── Sécurité ──────────────────────────────────────────────────────────────
    object Security2FA   : Destination() { override val route = "account/security/2fa" }

    // ── Compte ────────────────────────────────────────────────────────────────
    object Profile       : Destination() { override val route = "profile" }
    object OrdersHistory : Destination() { override val route = "orders-history" }
}
```

---

## Carte de navigation complète

```
startDestination
    ├── token != null → "orders-history"
    └── token == null → "login"

"login"
    ├── onNavigateToRegister       → "register"
    ├── onNavigateToForgotPassword → "forgot-password"
    └── onLoginSuccess             → "orders-history" { popUpTo("login") }

"register"
    ├── onNavigateToLogin          → "login"
    └── onRegisterSuccess(email)   → "confirm-email?email=xxx" { popUpTo("register") }

"forgot-password"
    ├── onNavigateToResetPassword(email) → "reset-password?email=xxx"
    └── onNavigateToLogin                → popBackStack()

"reset-password?email={email}"
    └── onNavigateToLogin          → "login" { popUpTo(0) }

"confirm-email?email={email}"
    └── onNavigateToLogin          → "login" { popUpTo(0) }

"account/security/2fa"
    ├── onNavigateToAdmin          → popBackStack()
    └── onNavigateToProfile        → popBackStack()

"orders-history"  ──→  AccountSection(ORDERS)
"profile"         ──→  AccountSection(PROFILE)
```

---

## Navigation pilotée par le token

```kotlin
val token by sessionManager.token.collectAsState()
// token != null → OrdersHistory, token == null → Login
// clearSession() → token = null → rebasculement automatique sur Login
```

---

## Navigation avec argument email (OTP flows)

Les écrans OTP reçoivent l'email pré-rempli via un query param URL-encodé.

```kotlin
// Depuis RegisterScreen → ConfirmEmailScreen
navController.navigateToRoute(Destination.ConfirmEmail.withEmail(email)) {
    popUpTo(Destination.Register.route) { inclusive = true }
}

// Depuis ForgotPasswordScreen → ResetPasswordScreen
navController.navigateToRoute(Destination.ResetPassword.withEmail(email))
```

Lecture dans le NavHost :

```kotlin
composable(Destination.ResetPassword) { backStack ->
    val email = URLDecoder.decode(
        backStack.arguments?.getString("email") ?: "", "UTF-8"
    )
    ResetPasswordScreen(navController = navController, initialEmail = email, ...)
}
```

Les ViewModels reçoivent l'email via `initEmail(email: String)` appelé dans un `LaunchedEffect` :

```kotlin
LaunchedEffect(initialEmail) { viewModel.initEmail(initialEmail) }

// initEmail est idempotent : ne remplace que si le champ est vide
fun initEmail(email: String) {
    if (state.value.email.isBlank() && email.isNotBlank()) {
        updateState { copy(email = email) }
    }
}
```

---

## Navigation secondaire — AccountSection

```kotlin
@Composable
fun AccountSection(
    initialTab: NavTab = NavTab.ORDERS,
    onNavigateTo2FA: () -> Unit = {}       // ← propagé depuis le NavHost principal
) {
    NavHost(startDestination = initialTab.route) {
        composable("orders")  { OrderHistoryScreen(innerNav) }
        composable("profile") { ProfileScreen(innerNav, onNavigateTo2FA = onNavigateTo2FA) }
    }
}
```

`ProfileScreen` affiche le bouton "Configurer 2FA" uniquement pour les rôles `Admin` / `SuperAdmin`, et appelle `onNavigateTo2FA` qui remonte jusqu'au NavHost principal via `navigateTo(Destination.Security2FA)`.

---

## Helpers de navigation

```kotlin
// Navigation standard vers une Destination
fun NavController.navigateTo(destination: Destination, navOptions: NavOptions? = null)

// Avec builder (popUpTo, launchSingleTop…)
fun NavController.navigateTo(destination: Destination, builder: NavOptionsBuilder.() -> Unit)

// Pour les routes paramétriques (OTP flows)
fun NavController.navigateToRoute(route: String, builder: NavOptionsBuilder.() -> Unit = {})

// Extension NavGraphBuilder
fun NavGraphBuilder.composable(destination: Destination, content: @Composable (NavBackStackEntry) -> Unit)
```

---

## Ajouter une destination

### 1. Déclarer

```kotlin
// Sans argument
object Notifications : Destination() { override val route = "notifications" }

// Avec argument
object NotificationDetail : Destination() {
    override val route = "notifications/{id}"
    override val arguments = listOf(navArgument("id") { type = NavType.StringType })
    fun routeWith(id: String) = "notifications/$id"
}
```

### 2. Enregistrer dans NavHost

```kotlin
composable(Destination.Notifications) { NotificationsScreen(navController) }

composable(Destination.NotificationDetail) { backStack ->
    val id = backStack.arguments?.getString("id") ?: return@composable
    NotificationDetailScreen(navController, id)
}
```

### 3. Naviguer

```kotlin
navController.navigateTo(Destination.Notifications)
navController.navigate(Destination.NotificationDetail.routeWith(notif.id))
```

---

## BottomNavBar

```kotlin
enum class NavTab { ORDERS, PROFILE }

// Items : Orders · Profile · Sign out (destructive)
// L'item "Sign out" (tab = null) appelle onLogout → authRepository.logout()
```