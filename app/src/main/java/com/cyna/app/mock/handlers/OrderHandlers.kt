package com.cyna.app.mock.handlers

import com.cyna.app.mock.factories.*
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*

// ---------------------------------------------------------------------------
// In-memory stores
// ---------------------------------------------------------------------------

private val _subscriptions: List<MockSubscription> =
    MockFactories.makeMany(3) { MockFactories.makeSubscription(status = "active") }

// One of every status + a few random orders — mirrors orders-account.js
private val _accountOrders: MutableList<MockOrder> = mutableListOf(
    MockFactories.makeOrder("active"),
    MockFactories.makeOrder("paid"),
    MockFactories.makeOrder("terminated"),
    MockFactories.makeOrder("refunded"),
    MockFactories.makeOrder("pending"),
    MockFactories.makeOrder("failed"),
    MockFactories.makeOrder(),
    MockFactories.makeOrder(),
)

// ---------------------------------------------------------------------------
// Subscription handlers
// ---------------------------------------------------------------------------

val subscriptionHandlers: List<MockHandler> = listOf(

    MockHandler(
        method = HttpMethod.Get,
        path = "/subscriptions",
        resolver = { _, _ -> _subscriptions }
    ),

    MockHandler(
        method = HttpMethod.Delete,
        path = "/subscriptions/:id",
        status = HttpStatusCode.NoContent,
        resolver = { _, _ -> null }
    ),
)

// ---------------------------------------------------------------------------
// Account order handlers — mirrors orders-account.js
// ---------------------------------------------------------------------------

val accountOrderHandlers: List<MockHandler> = listOf(

    MockHandler(
        method = HttpMethod.Get,
        path = "/account/orders",
        resolver = { _, _ -> _accountOrders.toList() }
    ),

    MockHandler(
        method = HttpMethod.Get,
        path = "/account/orders/:id",
        resolver = { params, _ -> _accountOrders.find { it.id == params["id"] } }
    ),
)
