package com.cyna.app.domain.model

data class AccountOrder(
    val id: String,
    val status: String,
    val statusLabel: String,
    val productName: String,
    val total: Double,
    val type: String,
    val paymentLast4: String,
    val paymentMethod: String,
    val invoiceUrl: String?,
    val createdAt: String
)
