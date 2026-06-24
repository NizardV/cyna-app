# Couche mock et développement local

## Vue d'ensemble

La couche mock permet de développer sans serveur backend. Activée via `MOCK_API=true` dans `local.properties`.

```
Application.onCreate()
    │
    ├─ if (MOCK_API) MockInitializer.init()
    │       └─ MockRegistry.registerMany(
    │               authHandlers        ← login, register, logout, me,
    │               + twoFactorHandlers    forgot-pwd, reset-pwd, confirm-email
    │               + userHandlers      ← profile, password, subscriptions, orders
    │               + subscriptionHandlers
    │               + accountOrderHandlers
    │          )
    │
    └─ startKoin { appModule }
            └─ HttpClientEngine = buildMockEngine(400ms)
```

---

## Activation

```properties
# local.properties
MOCK_API=true
```

En mode mock : aucune requête réseau, délai 400 ms simulé, erreurs aléatoires selon les handlers.

---

## Handlers enregistrés

### AuthHandlers

| Méthode | Route | Comportement mock |
|---------|-------|------------------|
| `POST` | `/auth/login` | OK. Email `error@example.com` → erreur |
| `POST` | `/auth/register` | OK toujours |
| `POST` | `/auth/refresh` | OK toujours |
| `POST` | `/auth/logout` | 25% d'erreur simulée |
| `GET` | `/auth/me` | Renvoie `makeDemoUser()` (Jean Dupont) |
| `POST` | `/auth/forgot-password` | Toujours 200 (anti-énumération) |
| `POST` | `/auth/reset-password` | Code `"000000"` → erreur, sinon OK |
| `POST` | `/auth/confirm-email` | Code `"000000"` → erreur, sinon OK |

### TwoFactorHandlers

| Méthode | Route | Comportement mock |
|---------|-------|------------------|
| `POST` | `/auth/2fa/setup` | Renvoie secret fixe `JBSWY3DPEHPK3PXP` + otpAuthUrl |
| `POST` | `/auth/2fa/confirm` | Code `"000000"` → erreur, sinon OK |

```kotlin
// TwoFactorHandlers.kt
val twoFactorHandlers: List<MockHandler> = listOf(
    MockHandler(
        method = HttpMethod.Post,
        path   = "/auth/2fa/setup",
        resolver = { _, _ ->
            TwoFactorSetupDto(
                secret     = "JBSWY3DPEHPK3PXP",
                otpAuthUrl = "otpauth://totp/Cyna:admin@cyna.io?secret=JBSWY3DPEHPK3PXP&issuer=Cyna"
            )
        }
    ),
    MockHandler(
        method = HttpMethod.Post,
        path   = "/auth/2fa/confirm",
        resolver = { _, body ->
            val code = body?.let { Json.parseToJsonElement(it).jsonObject }
                ?.get("totpCode")?.jsonPrimitive?.content ?: ""
            if (code == "000000") error("Code TOTP invalide.")
            MessageResponse("Authentification à deux facteurs activée avec succès.")
        }
    )
)
```

### UserHandlers

| Méthode | Route | Comportement mock |
|---------|-------|------------------|
| `GET` | `/user/profile` | État `_currentUser` en mémoire |
| `PUT` | `/user/profile` | Met à jour `_currentUser`, 30% d'erreur |
| `PUT` | `/user/password` | Valide `currentPassword` non vide, 30% d'erreur |
| `GET` | `/user/subscriptions` | 3 abonnements `Active` |
| `DELETE` | `/user/subscriptions/:id` | 204 No Content |
| `GET` | `/user/orders` | 8 commandes préchargées |
| `GET` | `/user/orders/:id` | Recherche par id |

---

## Codes de test déterministes

| Scénario | Valeur à utiliser |
|----------|------------------|
| Email d'erreur login | `error@example.com` |
| Code OTP invalide (reset, confirm, 2fa) | `000000` |
| Code OTP valide | N'importe quel code ≠ `000000` |
| Erreur aléatoire profil (30%) | Tenter plusieurs fois `PUT /user/profile` |

---

## MockRegistry — API

```kotlin
object MockRegistry {
    fun register(handler: MockHandler): MockRegistry
    fun registerMany(newHandlers: List<MockHandler>): MockRegistry
    fun resolve(method: HttpMethod, path: String): Pair<MockHandler, Map<String, String>>?
    fun listRoutes(): List<String>
    fun clear(): MockRegistry
}
```

### MockHandler

```kotlin
data class MockHandler(
    val method: HttpMethod,
    val path: String,                              // ex. "/auth/2fa/confirm"
    val status: HttpStatusCode = HttpStatusCode.OK,
    val resolver: suspend (
        params: Map<String, String>,               // path + query params fusionnés
        body: String?                              // body JSON brut
    ) -> Any?
)
```

---

## MockFactories — données générées

```kotlin
object MockFactories {
    fun makeDemoUser(): MockUser           // Jean Dupont fixe
    fun makeUser(...): MockUser            // aléatoire
    fun makeCategory(): MockCategory
    fun makeProduct(): MockProduct         // status pondéré (3x Active)
    fun makeOrderItem(): MockOrderItem
    fun makeOrder(status: String): MockOrder
    fun makeSubscription(status: String = "Active"): MockSubscription
    fun makeAuthResponse(): MockAuthResponse
    fun <T> makeMany(n: Int, factory: () -> T): List<T>
}
```

---

## Ajouter un handler mock

### 1. Créer le fichier

```kotlin
// mock/handlers/NotificationHandlers.kt
val notificationHandlers: List<MockHandler> = listOf(
    MockHandler(
        method   = HttpMethod.Get,
        path     = "/notifications",
        resolver = { _, _ ->
            MockFactories.makeMany(5) {
                mapOf("id" to java.util.UUID.randomUUID().toString(),
                      "message" to "Alerte de sécurité", "read" to false)
            }
        }
    )
)
```

### 2. Enregistrer dans MockInitializer

```kotlin
MockRegistry.registerMany(
    authHandlers
    + twoFactorHandlers
    + userHandlers
    + subscriptionHandlers
    + accountOrderHandlers
    + notificationHandlers    // ← ajouter ici
)
```

---

## MockEngine — fonctionnement interne

```
Requête sortante : GET https://api.../api/user/orders
    │
    1. path = "/api/user/orders" → strip "/api" → "/user/orders"
    2. MockRegistry.resolve(GET, "/user/orders")
    3. handler.resolver({}, null) → List<MockOrder>
    4. Sérialise via kotlinx.serialization
    5. Réponse : 200 OK, Content-Type: application/json
```

Erreur dans le resolver → `respond(500, {"error": "…"})` → `HttpCallValidator` lance toast + vibration.