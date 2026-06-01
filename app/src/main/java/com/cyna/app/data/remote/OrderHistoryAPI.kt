package com.cyna.app.data.remote

import com.cyna.app.data.dto.AccountOrderDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json

internal class OrderHistoryAPI(private val client: HttpClient) {

    suspend fun getAccountOrders(): List<AccountOrderDto> {
        val response = client.get("account/orders")
            .accept(HttpStatusCode.OK)

        val rawBody = response.bodyAsText()
        println("RAW JSON: $rawBody")  // visible in Logcat

        return Json.decodeFromString(rawBody)
    }
}