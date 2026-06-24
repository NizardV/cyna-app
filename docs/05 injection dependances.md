# Injection de dépendances — Koin

## Vue d'ensemble

Le projet utilise **Koin 4.2.1**. Toutes les déclarations sont dans `di/AppModule.kt`.

```
Application.onCreate()
        │
        ├─ if (MOCK_API) MockInitializer.init()   ← avant Koin
        │
        └─ startKoin { modules(appModule) }
                └── AppModule
                        ├─ SessionManager
                        ├─ VibrationHelper
                        ├─ HttpClientEngine  (Mock | OkHttp | CIO)
                        ├─ HttpClient
                        ├─ AuthAPI / UserAPI / OrderHistoryAPI / TwoFactorAPI
                        └─ AuthRepository / UserRepository
                           OrderHistoryRepository / TwoFactorRepository
```

---

## AppModule — déclarations complètes

```kotlin
val appModule = module {

    single { SessionManager(androidContext()) }
    single { VibrationHelper(androidContext()) }

    // ── Moteur HTTP ──────────────────────────────────────────────────────────
    single<HttpClientEngine> {
        when {
            BuildConfig.MOCK_API -> buildMockEngine(delayMs = 400L)
            BuildConfig.DEBUG -> {
                val tm = trustAllTrustManager()
                val sslContext = SSLContext.getInstance("TLS")
                    .apply { init(null, arrayOf(tm), SecureRandom()) }
                OkHttp.create {
                    preconfigured = OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.socketFactory, tm)
                        .hostnameVerifier { _, _ -> true }
                        .build()
                }
            }
            else -> CIO.create()
        }
    }

    // ── Client HTTP ──────────────────────────────────────────────────────────
    single<HttpClient> {
        createHttpClient(
            baseUrl         = BuildConfig.BASE_URL,
            engine          = get(),
            vibrationHelper = get(),
            sessionManager  = get()
        )
    }

    // ── APIs ─────────────────────────────────────────────────────────────────
    single { AuthAPI(get()) }
    single { UserAPI(get()) }
    single { OrderHistoryAPI(get()) }
    single { TwoFactorAPI(get()) }          // ← nouveau

    // ── Repositories ─────────────────────────────────────────────────────────
    single<AuthRepository>         { AuthRepositoryImpl(get(), get(), get()) }
    single<UserRepository>         { UserRepositoryImpl(get()) }
    single<OrderHistoryRepository> { OrderHistoryRepositoryImpl(get()) }
    single<TwoFactorRepository>    { TwoFactorRepositoryImpl(get()) }  // ← nouveau
}
```

---

## Injection dans les ViewModels

Les ViewModels étendent `KViewModel` (Kindling :compose) qui étend `KoinComponent`. Les dépendances sont résolues via `by inject()` :

```kotlin
class Security2FAViewModel(application: Application) :
    KViewModel<Security2FAContracts.UiState>(...) {

    private val twoFactorRepository: TwoFactorRepository by inject()
}

class ForgotPasswordViewModel(application: Application) :
    KViewModel<ForgotPasswordContracts.UiState>(...) {

    private val authRepository: AuthRepository by inject()
}
```

Avantage : aucune déclaration dans `AppModule` pour les ViewModels — ils sont instanciés par `viewModel<MyVM>()` de Compose.

---

## Injection dans les Composables

```kotlin
@Composable
fun NavHost(...) {
    val sessionManager: SessionManager = koinInject()
    val token by sessionManager.token.collectAsState()
}

@Composable
fun AccountSection(...) {
    val authRepository: AuthRepository = koinInject()
}
```

---

## Ajouter une nouvelle dépendance

### 1. Interface (domain)

```kotlin
interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
}
```

### 2. API + Implémentation (data)

```kotlin
internal class NotificationAPI(private val client: HttpClient) {
    suspend fun getNotifications(): List<NotificationDto> =
        client.get("notifications").accept(HttpStatusCode.OK).body()
}

internal class NotificationRepositoryImpl(
    private val api: NotificationAPI
) : NotificationRepository {
    override suspend fun getNotifications() =
        api.getNotifications().map { it.toDomain() }
}
```

### 3. AppModule

```kotlin
single { NotificationAPI(get()) }
single<NotificationRepository> { NotificationRepositoryImpl(get()) }
```

### 4. ViewModel

```kotlin
private val notificationRepository: NotificationRepository by inject()
```

---

## Dépannage

| Symptôme | Cause | Solution |
|----------|-------|---------|
| `NoBeanDefFoundException` | Déclaration manquante | Ajouter `single { }` dans `AppModule` |
| Crash `by inject()` au démarrage | Koin non initialisé | Vérifier `Application.kt` dans `AndroidManifest` |
| `TwoFactorRepository` non trouvé | Oubli de la déclaration | `single<TwoFactorRepository> { TwoFactorRepositoryImpl(get()) }` |