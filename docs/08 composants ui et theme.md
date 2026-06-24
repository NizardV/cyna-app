# Composants UI et thème

## Architecture UI

```
ui/core/
├── theme/
│   ├── Color.kt           ← Palette Cyna (oklch → sRGB hex)
│   ├── Theme.kt           ← LightColorScheme, DarkColorScheme, AppTheme
│   ├── Type.kt            ← CynaTypography (Material 3)
│   └── ThemeManager.kt    ← Light | Dark | System
│
└── components/ui/
    ├── AccountSection.kt       ← Shell onglets Orders / Profile
    ├── AuthCard.kt             ← Carte blanche auth (rounded-2xl, shadow-sm)
    ├── BottomNavBar.kt         ← Barre de navigation inférieure
    ├── CancelDialog.kt         ← Dialog de confirmation résiliation
    ├── FieldWithLabel.kt       ← KInput + KLabel empilés
    ├── KLink.kt                ← Texte cliquable souligné
    ├── SectionCard.kt          ← Carte avec titre, description, contenu
    ├── layout/
    │   ├── CenteredBox.kt
    │   ├── CenteredColumn.kt
    │   ├── Footer.kt
    │   ├── Header.kt
    │   ├── MainScaffold.kt
    │   └── Spacers.kt
    ├── order/
    │   ├── row.kt              ← OrderRow, OrderRowSkeleton
    │   └── Year.kt             ← YearGroup, YearFilterRow
    └── profile/
        └── subscription.kt     ← SubscriptionRow
```

---

## Thème — AppTheme

```kotlin
// MainActivity.kt
setContent {
    AppTheme {          // ← wrape toute l'application
        App()
        Toaster()       // ← notifications globales (Kindling)
    }
}
```

`AppTheme` lit `ThemeManager.isDarkTheme()` et applique le bon `ColorScheme`.

### Gestion du thème

```kotlin
// Basculer Light → Dark → System → Light
ThemeManager.toggleTheme()

// Forcer un mode
ThemeManager.setTheme(ThemeState.Light)
ThemeManager.setTheme(ThemeState.Dark)
ThemeManager.setTheme(ThemeState.System)   // suit le système Android
```

---

## Palette de couleurs

Définie dans `Color.kt` — conversion exacte depuis le CSS Cyna (oklch → sRGB via culori).

### Mode clair

| Token Material | Couleur Cyna | Valeur hex |
|---------------|-------------|-----------|
| `primary` | Violet Cyna | `#562BF5` |
| `onPrimary` | Blanc cassé | `#F8F8FC` |
| `background` | Gris-bleu très clair | `#F9FAFC` |
| `surface` | Blanc pur | `#FFFFFF` |
| `outline` | Gris-bleu | `#D5D7E0` |
| `error` | Rouge | `#DF202E` |

### Mode sombre

| Token Material | Couleur Cyna | Valeur hex |
|---------------|-------------|-----------|
| `primary` | Violet lumineux | `#6D55FF` |
| `background` | Noir-bleu profond | `#020205` |
| `surface` | Très sombre | `#08080F` |
| `outline` | Blanc 10% | `#1AFFFFFF` |
| `error` | Rouge vif | `#F94144` |

### Règle d'utilisation

```kotlin
// ✅ Toujours via les tokens du thème
Text(color = MaterialTheme.colorScheme.primary)
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface))
Surface(color = MaterialTheme.colorScheme.error.copy(.07f))

// ❌ Jamais de valeur codée en dur (sauf éléments de branding)
Text(color = Color(0xFF562BF5))
```

---

## Composants réutilisables

### AuthCard

Carte blanche standardisée pour tous les écrans d'authentification. Miroir exact du composant web `rounded-2xl border border-gray-100 bg-white p-8 shadow-sm`.

```kotlin
AuthCard(modifier = Modifier) {
    // ColumnScope — contenu de la carte
    Icon(...)
    Text("Titre")
    FieldWithLabel(...)
    KButton(...)
}
```

Utilisé dans : `LoginScreen`, `RegisterScreen`, `ForgotPasswordScreen`, `ResetPasswordScreen`, `ConfirmEmailScreen`, `Security2FAScreen`.

### InputOTP (Kindling :core)

Saisie de code OTP à 6 chiffres, format 3+3 avec séparateur. Géré via `rememberInputOTPState`.

```kotlin
val otpState = rememberInputOTPState(
    value        = state.code,
    length       = 6,
    onValueChange = viewModel::onCodeChange
)

InputOTP(state = otpState, enabled = !state.isLoading) {
    InputOTPGroup {
        InputOTPSlot(otpState, 0, isFirst = true)
        InputOTPSlot(otpState, 1)
        InputOTPSlot(otpState, 2, isLast = true)
    }
    InputOTPSeparator()
    InputOTPGroup {
        InputOTPSlot(otpState, 3, isFirst = true)
        InputOTPSlot(otpState, 4)
        InputOTPSlot(otpState, 5, isLast = true)
    }
}
```

Utilisé dans : `ResetPasswordScreen`, `ConfirmEmailScreen`, `Security2FAScreen`.

### SectionCard

Carte structurée avec titre, description et contenu séparé par un divider.

```kotlin
SectionCard(
    title       = "Informations personnelles",
    description = "Gérez vos coordonnées et votre adresse e-mail."
) {
    // Contenu — ColumnScope
    FieldWithLabel("Prénom", state.firstName, onFirstNameChange)
    FieldWithLabel("Nom", state.lastName, onLastNameChange)
    KButton("Enregistrer", onClick = onSave)
}
```

### FieldWithLabel

Combine un `KLabel` (Kindling) et un `KInput` (Kindling) dans une colonne.

```kotlin
// Champ texte simple
FieldWithLabel(
    label         = "Adresse e-mail",
    value         = state.email,
    onValueChange = onEmailChange,
    placeholder   = "vous@exemple.com",
    isPassword    = false,
    isError       = state.emailError != null,
    enabled       = !state.isLoading,
    modifier      = Modifier.fillMaxWidth()
)

// Champ mot de passe
FieldWithLabel(
    label         = "Mot de passe",
    value         = state.password,
    onValueChange = onPasswordChange,
    isPassword    = true,
    modifier      = Modifier.fillMaxWidth()
)

// Avec contenu à droite (ex. badge "Vérifié")
FieldMaskWithLabel(
    label             = "Email",
    value             = state.email,
    mask              = KMaskPattern.Email,
    onValueChange     = onEmailChange,
    onValidationChange = onEmailValidationChange,
    trailingContent   = {
        Spacer(Modifier.width(6.dp))
        VerifiedBadge()
    }
)
```

### KLink

Texte cliquable souligné couleur `primary`.

```kotlin
KLink(
    text    = "Créer un compte",
    onClick = onNavigateToRegister
)
```

### CancelDialog

Dialog de confirmation pour la résiliation d'un abonnement.

```kotlin
// Affiché si state.cancelTarget != null
state.cancelTarget?.let { sub ->
    CancelDialog(
        sub        = sub,
        cancelling = state.cancelling,
        onDismiss  = viewModel::dismissCancel,
        onConfirm  = viewModel::confirmCancel
    )
}
```

---

## Composants de layout

### MainScaffold

Scaffold standard avec `Header` (optionnel) et `Footer` (optionnel), scroll vertical intégré.

```kotlin
// Avec header + footer (écrans principaux)
MainScaffold(navController = navController) { innerPadding ->
    // contenu scrollable
}

// Sans header ni footer (auth)
MainScaffold(showLayout = false) { innerPadding ->
    // plein écran
}
```

### Spacers prédéfinis

```kotlin
SmallSpacer()         // 8 dp
MediumSpacer()        // 16 dp
LargeSpacer()         // 24 dp
ExtraLargeSpacer()    // 32 dp
CustomSpacer(48.dp)
```

### CenteredBox / CenteredColumn

```kotlin
// Box plein écran, contenu centré
CenteredBox(horizontalPadding = 24.dp) {
    LoginForm()
}

// Colonne centrée pleine largeur
CenteredColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Title()
    Subtitle()
}
```

---

## Composants d'écran — Orders

### OrderRow

Affiche une commande avec statut coloré, date, résumé des produits et bouton PDF.

```kotlin
OrderRow(
    order  = accountOrder,
    isLast = index == orders.size - 1   // masque le divider sur le dernier item
)
```

Statuts supportés : `"Paid"` · `"Pending"` · `"Failed"` · `"Refunded"` (PascalCase, enum .NET).

### YearGroup

Groupe des commandes par année avec un header.

```kotlin
YearGroup(
    year   = 2024,
    orders = ordersForYear,
    dimmed = year != currentYear   // atténue les années passées
)
```

### YearFilterRow

Chips de filtre par année.

```kotlin
YearFilterRow(
    years        = listOf(2026, 2025, 2024),
    selectedYear = state.selectedYear,   // "all" ou "2025"
    onYearChange = viewModel::onYearChange
)
```

---

## Composants d'écran — Profile

### SubscriptionRow

Affiche un abonnement actif avec badges plan / auto-renouvellement et bouton résiliation.

```kotlin
SubscriptionRow(
    sub      = subscription,
    onCancel = { viewModel.requestCancel(subscription) }
)
```

---

## Typographie

Définie dans `CynaTypography` (Type.kt), suit la spécification Material 3.

```kotlin
// Titres d'écran
Text(style = MaterialTheme.typography.headlineSmall)    // 24 sp

// Titres de section
Text(style = MaterialTheme.typography.titleMedium)      // 16 sp, Medium

// Corps de texte
Text(style = MaterialTheme.typography.bodyMedium)       // 14 sp
Text(style = MaterialTheme.typography.bodySmall)        // 12 sp

// Labels
Text(style = MaterialTheme.typography.labelSmall)       // 11 sp, Medium
```