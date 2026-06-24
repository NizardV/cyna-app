# ViewModel et gestion d'état — MVI

## Principe

Le projet suit un pattern **MVI (Model-View-Intent)** adapté à Compose. Chaque écran possède :

- un `UiState` — état immuable observé par le composable via `StateFlow`
- des fonctions de ViewModel — déclenchées comme callbacks depuis le composable
- des événements one-shot (`sendEvent`) — pour navigation ou messages ponctuels

```
Composable
    │  observe state (recompose à chaque changement)
    │  appelle vm::saveProfile, vm::onEmailChange…
    ▼
KViewModel<UiState>
    │  updateState { copy(…) }  → StateFlow
    │  fetchData { repository.… }
    │  sendEvent(Destination.X)
    ▼
KScreen (infrastructure Kindling)
    │  collecte les events
    │  route Destination → NavController.navigate()
```

---

## KViewModel — classe de base (Kindling Compose)

Tous les ViewModels étendent `KViewModel<State>` fourni par le module `kindling-compose` :

```kotlin
open class KViewModel<State>(
    initialState: State,
    application: Application
) : AndroidViewModel(application), KoinComponent {

    val state: StateFlow<State>   // état observable en lecture seule
    val events: Flow<Any>         // événements one-shot

    // Met à jour l'état de manière atomique
    protected fun updateState(block: State.() -> State)

    // Émet un événement one-shot (navigation, toast externe…)
    protected fun sendEvent(obj: Any)

    // Lance une coroutine IO, livre le résultat sur Main
    fun <T> fetchData(
        source: suspend () -> T,
        onResult: ResultScope<T>.() -> Unit
    )
}
```

---

## Créer un ViewModel — exemple complet

### 1. Définir les contracts

```kotlin
// ui/screens/catalog/CatalogViewModel.kt

interface CatalogContracts {
    data class UiState(
        val products: List<Product> = emptyList(),
        val loading: Boolean = true,
        val error: String? = null,
        val searchQuery: String = ""
    )
}
```

### 2. Implémenter le ViewModel

```kotlin
class CatalogViewModel(application: Application) :
    KViewModel<CatalogContracts.UiState>(CatalogContracts.UiState(), application) {

    private val repository: ProductRepository by inject()

    init { load() }

    private fun load() {
        fetchData(
            source = { repository.getProducts() },
            onResult = {
                onSuccess { products ->
                    updateState { copy(products = products, loading = false) }
                }
                onFailure { e ->
                    updateState { copy(loading = false, error = e.message) }
                }
            }
        )
    }

    fun onSearchChange(q: String) = updateState { copy(searchQuery = q) }

    fun retry() {
        updateState { copy(loading = true, error = null) }
        load()
    }
}
```

### 3. Connecter à l'écran

```kotlin
// Composable public — reçoit NavController
@Composable
fun CatalogScreen(navController: NavController) {
    KScreen(
        viewModel    = viewModel<CatalogViewModel>(),
        navController = navController
    ) { state, vm ->
        CatalogContent(
            state          = state,
            onSearchChange = vm::onSearchChange,
            onRetry        = vm::retry
        )
    }
}

// Composable privé — pur, testable, prévisualisable
@Composable
private fun CatalogContent(
    state: CatalogContracts.UiState = CatalogContracts.UiState(),
    onSearchChange: (String) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    // Pas de ViewModel, pas de NavController
    // Uniquement state + lambdas
    if (state.loading) { /* skeleton */ return }
    if (state.error != null) { /* error + retry button */ return }
    // …
}
```

---

## fetchData — opération unique

Pour les appels réseau ponctuels (GET, POST, DELETE, PUT) :

```kotlin
fetchData(
    source = { userRepository.updateProfile(firstName, lastName, email) },
    onResult = {
        onSuccess { user ->
            updateState { copy(savingProfile = false, user = user) }
            KToastManager.success("Profil mis à jour")
        }
        onFailure { e ->
            updateState { copy(savingProfile = false) }
            KToastManager.error("Erreur", e.message)
        }
    }
)
```

`fetchData` :
- s'exécute sur `Dispatchers.IO`
- livre le résultat sur `Dispatchers.Main`
- catchent toutes les exceptions → `onFailure`

---

## updateState — immuabilité garantie

```kotlin
// Toujours via copy() sur la data class
updateState { copy(loading = true, error = null) }

// Plusieurs champs en une seule mise à jour atomique
updateState {
    copy(
        savingProfile  = false,
        user           = updatedUser,
        firstNameInput = updatedUser.firstName,
        emailInput     = updatedUser.email
    )
}
```

---

## sendEvent — navigation one-shot

```kotlin
// Dans le ViewModel (rarement utilisé dans ce projet)
fun onLoginSuccess() {
    sendEvent(Destination.OrdersHistory)
}

// KScreen intercepte automatiquement les Destination et appelle navController.navigate()
```

---

## Patterns UiState courants

### État de chargement avec skeleton

```kotlin
data class UiState(
    val items: List<Item> = emptyList(),
    val loading: Boolean = true,   // ← true par défaut → skeleton immédiat
    val error: String? = null
)
```

```kotlin
// Dans le composable
when {
    state.loading -> ItemSkeleton()
    state.error != null -> ErrorCard(state.error, onRetry)
    state.items.isEmpty() -> EmptyState()
    else -> ItemList(state.items)
}
```

### Formulaire avec validation

```kotlin
data class UiState(
    // Champs formulaire
    val firstNameInput: String = "",
    val lastNameInput: String = "",
    val emailInput: String = "",
    val emailValid: Boolean = true,   // ← validé par le composant KMaskPattern.Email
    // État de sauvegarde
    val savingProfile: Boolean = false,
    // État de chargement initial (skeleton)
    val loadingUser: Boolean = true,
    // Modèle domaine chargé
    val user: User? = null
)
```

### Dialog de confirmation

```kotlin
data class UiState(
    val subscriptions: List<Subscription> = emptyList(),
    val cancelTarget: Subscription? = null,   // null = dialog fermé
    val cancelling: Boolean = false
)

// Ouvrir le dialog
fun requestCancel(sub: Subscription) = updateState { copy(cancelTarget = sub) }

// Fermer sans confirmer
fun dismissCancel() = updateState { copy(cancelTarget = null) }

// Confirmer
fun confirmCancel() {
    val target = state.value.cancelTarget ?: return
    updateState { copy(cancelling = true) }
    fetchData(
        source = { userRepository.cancelSubscription(target.id.toString()) },
        onResult = {
            onSuccess {
                updateState {
                    copy(
                        cancelling    = false,
                        cancelTarget  = null,
                        subscriptions = subscriptions.filter { it.id != target.id }
                    )
                }
                KToastManager.success("Abonnement résilié")
            }
            onFailure { e ->
                updateState { copy(cancelling = false, cancelTarget = null) }
                KToastManager.error("Erreur", e.message)
            }
        }
    )
}
```

---

## Règles à respecter

| Règle | Raison |
|-------|--------|
| `UiState` toujours `data class` avec valeurs par défaut | Permet la preview Compose et les tests sans paramètres |
| Ne jamais exposer `MutableStateFlow` | L'état ne doit être modifiable que depuis le ViewModel |
| `updateState` pour l'état persistant, `sendEvent` pour le one-shot | Clarté des intentions |
| `loadingXxx = true` par défaut dans `UiState` | Skeleton affiché immédiatement, pas de flash vide |
| Le composable privé `Content` ne reçoit jamais de ViewModel | Testabilité et prévisualisabilité |
| Initialiser les données dans `init {}` du ViewModel | Pas de `LaunchedEffect` pour les données initiales |