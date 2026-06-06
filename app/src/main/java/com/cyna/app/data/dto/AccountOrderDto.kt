package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountOrderDto(
    val id: String,
    val userId: String,
    val status: String,
    val statusLabel: String,
    val productName: String,
    val total: Double,
    val type: String,
    val paymentLast4: String,
    val paymentMethod: String,
    val invoiceUrl: String? = null,
    val createdAt: String
)