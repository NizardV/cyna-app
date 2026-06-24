# Kindling — Bibliothèque UI

> **Kindling** est la bibliothèque de composants maison publiée sur Maven Central sous  
> `io.github.clementbobin.kindling` — version actuelle : **1.0.9**

---

## Architecture des modules

```
Kindling
│
├── :core         → Composants UI Jetpack Compose (boutons, inputs, dialogs…)
│                   Dépend de : Compose Material 3
│
├── :utils        → Fonctions utilitaires communes (formatage, dates, prix…)
│                   Dépend de : Kotlin stdlib uniquement
│
├── :android      → Capacités natives Android (vibration, son, permissions…)
│                   Dépend de : Android SDK
│
└── :compose      → Infrastructure KMP Kotlin Multiplatform
                    (KViewModel, KScreen, routing…)
                    Dépend de : :core, Compose, Koin
```

### Déclarations Gradle

```toml
# gradle/libs.versions.toml
[versions]
kindling = "1.0.9"

[libraries]
kindling-core    = { group = "io.github.clementbobin.kindling", name = "core",    version.ref = "kindling" }
kindling-utils   = { group = "io.github.clementbobin.kindling", name = "utils",   version.ref = "kindling" }
kindling-compose = { group = "io.github.clementbobin.kindling", name = "compose", version.ref = "kindling" }
```

```kotlin
// app/build.gradle.kts
implementation(libs.kindling.core)
implementation(libs.kindling.compose)
implementation(libs.kindling.utils)
```

---

## Module :core — Composants UI

Bibliothèque de composants Jetpack Compose inspirée de shadcn/ui, construite sur Material 3.

### KButton

Bouton avec variantes, tailles, état de chargement et contenu personnalisable.

```kotlin
// Texte simple
KButton(
    text      = "Enregistrer",
    onClick   = onSave,
    isLoading = state.saving,
    modifier  = Modifier.fillMaxWidth()
)

// Contenu composable (icône + texte)
KButton(
    onClick  = onDownload,
    variant  = KButtonVariant.Outline,
    size     = KButtonSize.Xs
) {
    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(11.dp))
    Spacer(Modifier.width(3.dp))
    Text("PDF", fontSize = 10.sp)
}
```

**Variantes :**

| Variant | Usage |
|---------|-------|
| `KButtonVariant.Default` | Action principale (rempli, couleur `primary`) |
| `KButtonVariant.Outline` | Action secondaire (bordure, fond transparent) |
| `KButtonVariant.Ghost` | Action discrète (pas de bordure ni fond) |
| `KButtonVariant.Destructive` | Action dangereuse (couleur `error`) |

**Tailles :**

| Taille | Usage |
|--------|-------|
| `KButtonSize.Default` | Standard |
| `KButtonSize.Sm` | Compact (cartes, dialogs) |
| `KButtonSize.Xs` | Très compact (badges, rows) |
| `KButtonSize.IconXs` | Icône seule, très petit |

### KInput

Champ de texte avec options de style, placeholder, icônes, mot de passe.

```kotlin
KInput(
    value         = state.searchQuery,
    onValueChange = onSearchChange,
    placeholder   = "Rechercher…",
    leadingIcon   = {
        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(15.dp))
    },
    trailingIcon  = if (state.searchQuery.isNotBlank()) {
        { ClearButton(onClick = { onSearchChange("") }) }
    } else null,
    modifier = Modifier.fillMaxWidth()
)

// Mot de passe (toggle visibilité intégré)
KInput(
    value         = state.password,
    onValueChange = onPasswordChange,
    isPassword    = true,
    isError       = state.passwordError != null
)
```

### KLabel

Label de champ de formulaire.

```kotlin
KLabel("Adresse e-mail")
```

### MaskInput

Champ avec masque de saisie (email, téléphone, date…).

```kotlin
MaskInput(
    value             = state.email,
    onValueChange     = onEmailChange,
    mask              = KMaskPattern.Email,
    onValidationChange = onEmailValidationChange,   // (Boolean) -> Unit
    isError           = state.emailError != null,
    modifier          = Modifier.fillMaxWidth()
)
```

**Patterns disponibles :** `KMaskPattern.Email` · `KMaskPattern.Phone` · `KMaskPattern.Date` · pattern custom via `customPattern`.

### KBadge

Badge coloré avec variante personnalisable.

```kotlin
// Variante custom (couleurs dynamiques selon le thème)
KBadge(
    variant = KBadgeVariant(
        bg = { Color(0xFF166534).copy(alpha = .12f) },
        fg = { Color(0xFF166534) }
    )
) {
    Text("Payé", fontSize = 10.sp)
}
```

### Skeleton

Placeholder animé pour les états de chargement.

```kotlin
// Rectangle skeleton
Skeleton(modifier = Modifier.fillMaxWidth(.55f).height(12.dp))

// Utilisation dans un OrderRowSkeleton
@Composable
fun OrderRowSkeleton() {
    Row(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Skeleton(modifier = Modifier.fillMaxWidth(.55f).height(12.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(.75f).height(10.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(.45f).height(10.dp))
        }
        Skeleton(modifier = Modifier.width(56.dp).height(18.dp))
    }
}
```

### Dialog (DialogContent, DialogHeader, DialogTitle…)

Système de dialog composable.

```kotlin
DialogContent(open = true, onDismiss = onDismiss) {
    DialogHeader {
        DialogTitle("Résilier l'abonnement")
        DialogDescription("Cette action est irréversible.")
    }
    Spacer(Modifier.height(12.dp))
    // … contenu custom …
    DialogFooter {
        KButton("Annuler", onClick = onDismiss, variant = KButtonVariant.Outline, size = KButtonSize.Sm)
        KButton("Confirmer", onClick = onConfirm, variant = KButtonVariant.Destructive, size = KButtonSize.Sm)
    }
}
```

### KToastManager — Notifications

Système de toasts globaux. Le `Toaster()` doit être placé à la racine de l'app.

```kotlin
// Placement (MainActivity / App composable)
Surface { NavHost(navController) }
Toaster()   // ← en dehors du NavHost, visible sur tous les écrans

// Utilisation depuis n'importe où (ViewModel, handler réseau…)
KToastManager.success("Profil mis à jour")
KToastManager.warning("Champs invalides", "Veuillez corriger les erreurs.")
KToastManager.error("Erreur serveur", "Réessayez plus tard.")
```

---

## Module :utils — Fonctions utilitaires

Fonctions helpers pures, sans dépendance Android.

### Formatage

```kotlin
import dev.kindling.utils.method.formatDate
import dev.kindling.utils.method.formatPrice
import dev.kindling.utils.method.getYear

// Formate une date ISO 8601 → lisible
formatDate("2024-03-15T10:30:00Z")   // → "15 mars 2024"

// Formate un prix Double → devise
formatPrice(249.99)                   // → "249,99 €"

// Extrait l'année d'une date ISO
getYear("2024-03-15T10:30:00Z")       // → 2024
```

Ces fonctions sont utilisées directement dans les composables :

```kotlin
// Dans OrderRow
Text(text = formatDate(order.createdAt))
Text(text = formatPrice(order.totalAmount))

// Dans YearGroup
val grouped = filtered.groupBy { getYear(it.createdAt) }

// Dans SubscriptionRow
Text("Renouvellement le ${formatDate(sub.currentPeriodEnd)}")
```

---

## Module :android — Capacités natives

Composants nécessitant le SDK Android (API niveau minimum respecté).

### VibrationHelper

Feedback haptique sémantique. Déclaré comme singleton Koin dans `AppModule`.

```kotlin
class VibrationHelper(context: Context) {

    // Double impulsion forte — erreur serveur (5xx), erreur réseau
    fun error()

    // Impulsion modérée — erreur client (4xx), validation échouée
    fun warning()

    // Impulsion douce — opération réussie
    fun success()

    // Tick léger — feedback UI générique
    fun light()
}
```

**Utilisé automatiquement** par `createHttpClient()` — pas besoin de l'appeler manuellement dans les ViewModels pour les erreurs réseau.

```kotlin
// Utilisation manuelle si besoin
val vibrationHelper: VibrationHelper by inject()
vibrationHelper.success()
```

**Compatibilité :**
- API ≥ 26 : `VibrationEffect.createWaveform()` avec contrôle d'amplitude
- API ≥ 31 : `VibratorManager` pour le vibrateur par défaut
- API < 26 : fallback sur `vibrator.vibrate(durationMs)` (deprecated)
- Appareils sans vibrateur : pas de crash (vérification `hasVibrator()`)

**Permission requise dans `AndroidManifest.xml` :**

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

---

## Module :compose — Infrastructure KMP

Fournit les classes de base pour l'architecture MVI.

### KViewModel

Classe de base de tous les ViewModels de l'application. Étend `AndroidViewModel` et `KoinComponent`.

```kotlin
class MyViewModel(application: Application) :
    KViewModel<MyContracts.UiState>(MyContracts.UiState(), application) {

    // Injection Koin
    private val repository: MyRepository by inject()

    // État
    // val state: StateFlow<UiState>  — hérité, lecture seule

    // Mise à jour de l'état (thread-safe)
    // protected fun updateState(block: UiState.() -> UiState)

    // Événement one-shot (navigation, toasts externes)
    // protected fun sendEvent(obj: Any)

    // Opération asynchrone IO → résultat sur Main
    // fun <T> fetchData(source, onResult)
}
```

### KScreen

Wrapper Composable qui connecte un `KViewModel` à un `NavController`.

```kotlin
@Composable
fun MyScreen(navController: NavController) {
    KScreen(
        viewModel     = viewModel<MyViewModel>(),
        navController = navController
    ) { state, vm ->
        // state : UiState observé (recompose à chaque changement)
        // vm    : instance du ViewModel
        MyContent(state = state, onAction = vm::doAction)
    }
}
```

`KScreen` gère en interne :
- Collecte du `StateFlow<UiState>` → recomposition
- Collecte des `events` → route les `Destination` vers `navController.navigate()`
- Gestion du bouton retour (si `onBack` fourni)