# Couche réseau — Ktor

## Vue d'ensemble

```
ViewModel
    └── Repository (interface domain)
            └── RepositoryImpl (data)
                    └── XxxAPI
                            └── HttpClient (Ktor)
                                    ├── MockEngine  (MOCK_API=true)
                                    ├── OkHttp      (DEBUG=true)
                                    └── CIO         (release)
```

Tous les appels réseau passent par un `HttpClient` Ktor unique, singleton Koin, configuré dans `data/remote/HttpClient.kt`.

---

## Configuration du client — `createHttpClient()`

```kotlin
fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create(),
    vibrationHelper: VibrationHelper? = null,
    sessionManager: SessionManager? = null
): HttpClient
```

### Plugins installés

| Plugin | Configuration | Rôle |
|--------|--------------|------|
| `ContentNegotiation` | `ignoreUnknownKeys = true`, `coerceInputValues = true` | JSON ↔ @Serializable |
| `HttpTimeout` | connect / socket / request = **15 s** | Évite les blocages |
| `Logging` | `LogLevel.ALL` | Debug réseau complet |
| `HttpCallValidator` | voir section Erreurs | Normalisation 4xx/5xx |
| `HttpCookies` | `SessionManagerCookieStorage` | Auth par cookie (si `sessionManager != null`) |

---

## Créer une classe API

Chaque ressource API a sa propre classe dans `data/remote/`. Elle reçoit le `HttpClient` injecté par Koin.

```kotlin
internal class ProductAPI(private val client: HttpClient) {

    // GET /products → List<ProductDto>
    suspend fun getProducts(): List<ProductDto> =
        client.get("products")
              .accept(HttpStatusCode.OK)
              .body()

    // GET /products/:id
    suspend fun getProduct(id: String): ProductDto =
        client.get("products/$id")
              .accept(HttpStatusCode.OK)
              .body()

    // POST /products
    suspend fun createProduct(dto: CreateProductDto): ProductDto =
        client.post("products") {
            setBodyJson(dto)          // helper — sérialise + Content-Type: application/json
        }.accept(HttpStatusCode.Created).body()

    // DELETE /products/:id
    suspend fun deleteProduct(id: String) =
        client.delete("products/$id")
              .accept(HttpStatusCode.NoContent)
}
```

### Helpers disponibles

```kotlin
// Sérialise le body en JSON + applique Content-Type automatiquement
inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T)

// Lance HttpException.NotAccepted si le statut n'est pas dans la liste
fun HttpResponse.accept(vararg codes: HttpStatusCode): HttpResponse
```

---

## DTOs — Data Transfer Objects

Les DTOs sont des `data class @Serializable internal` dans `data/dto/`. Ils reflètent exactement la forme JSON de l'API.

```kotlin
// data/dto/AccountOrderDto.kt
@Serializable
internal data class OrderItemDto(
    val id: Int,
    val productNameSnapshot: String,
    val planNameSnapshot: String,
    val quantityUsers: Int,
    val quantityDevices: Int
)

@Serializable
internal data class AccountOrderDto(
    val id: Int,
    val status: String,          // PascalCase : "Paid" | "Pending" | "Failed" | "Refunded"
    val totalAmount: Double,
    val createdAt: String,       // ISO 8601
    val invoiceUrl: String? = null,
    val items: List<OrderItemDto> = emptyList()
)
```

**Règles DTO :**
- Toujours `internal` — jamais exposés hors de `data/`
- Champs optionnels avec `= null` ou valeur par défaut
- Pas de logique métier — uniquement structure de données
- Nommage identique à l'API (camelCase côté Kotlin, configuré dans le serializer)

---

## Mapper DTO → Modèle domaine

Le mapping se fait dans l'implémentation du repository :

```kotlin
internal class OrderHistoryRepositoryImpl(
    private val orderHistoryAPI: OrderHistoryAPI
) : OrderHistoryRepository {

    override suspend fun getAccountOrders(): List<AccountOrder> =
        orderHistoryAPI.getAccountOrders().map { dto ->
            AccountOrder(
                id          = dto.id,
                status      = dto.status,
                totalAmount = dto.totalAmount,
                createdAt   = dto.createdAt,
                invoiceUrl  = dto.invoiceUrl,
                items       = dto.items.map { item ->
                    OrderItem(
                        id                  = item.id,
                        productNameSnapshot = item.productNameSnapshot,
                        planNameSnapshot    = item.planNameSnapshot,
                        quantityUsers       = item.quantityUsers,
                        quantityDevices     = item.quantityDevices
                    )
                }
            )
        }
}
```

---

## Gestion des erreurs

### Hiérarchie d'exceptions

```kotlin
sealed class HttpException(message: String) : Exception(message) {
    // Statut HTTP reçu non attendu (via .accept())
    class NotAccepted(message: String) : HttpException(message)

    // Réponse 4xx (400, 401, 403, 404…)
    class ClientError(val statusCode: Int, message: String) : HttpException(message)

    // Réponse 5xx
    class ServerError(val statusCode: Int, message: String) : HttpException(message)
}
```

### Traitement dans le ViewModel

```kotlin
fetchData(
    source = { productRepository.getProduct(id) },
    onResult = {
        onSuccess { product ->
            updateState { copy(product = product, loading = false) }
        }
        onFailure { error ->
            when (error) {
                is HttpException.ClientError -> when (error.statusCode) {
                    404 -> updateState { copy(error = "Produit introuvable") }
                    403 -> updateState { copy(error = "Accès refusé") }
                    else -> updateState { copy(error = error.message) }
                }
                is HttpException.ServerError ->
                    updateState { copy(error = "Erreur serveur, réessayez") }
                else ->
                    updateState { copy(error = "Erreur réseau") }
            }
        }
    }
)
```

---

## Ajouter une nouvelle ressource — procédure complète

### 1. DTO

```kotlin
// data/dto/NotificationDto.kt
@Serializable
internal data class NotificationDto(
    val id: String,
    val message: String,
    val read: Boolean,
    val createdAt: String
)
```

### 2. Classe API

```kotlin
// data/remote/NotificationAPI.kt
internal class NotificationAPI(private val client: HttpClient) {

    suspend fun getNotifications(): List<NotificationDto> =
        client.get("notifications").accept(HttpStatusCode.OK).body()

    suspend fun markAsRead(id: String): NotificationDto =
        client.post("notifications/$id/read").accept(HttpStatusCode.OK).body()
}
```

### 3. Interface repository (domain)

```kotlin
// domain/repository/NotificationRepository.kt
interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun markAsRead(id: String): Notification
}
```

### 4. Modèle domaine

```kotlin
// domain/model/Notification.kt
data class Notification(
    val id: String,
    val message: String,
    val read: Boolean,
    val createdAt: String
)
```

### 5. Implémentation repository

```kotlin
// data/repository/NotificationRepositoryImpl.kt
internal class NotificationRepositoryImpl(
    private val api: NotificationAPI
) : NotificationRepository {

    override suspend fun getNotifications(): List<Notification> =
        api.getNotifications().map { it.toDomain() }

    override suspend fun markAsRead(id: String): Notification =
        api.markAsRead(id).toDomain()
}

private fun NotificationDto.toDomain() = Notification(
    id        = id,
    message   = message,
    read      = read,
    createdAt = createdAt
)
```

### 6. Enregistrement Koin

```kotlin
// di/AppModule.kt
single { NotificationAPI(get()) }
single<NotificationRepository> { NotificationRepositoryImpl(get()) }
```

### 7. Handler mock (optionnel)

```kotlin
// mock/handlers/NotificationHandlers.kt
val notificationHandlers: List<MockHandler> = listOf(
    MockHandler(
        method = HttpMethod.Get,
        path   = "/notifications",
        resolver = { _, _ ->
            MockFactories.makeMany(5) {
                // structure identique à NotificationDto
                mapOf("id" to UUID.randomUUID().toString(), "message" to "Test", "read" to false)
            }
        }
    )
)
```

Puis ajouter dans `MockInitializer.init()` :

```kotlin
MockRegistry.registerMany(notificationHandlers)
```