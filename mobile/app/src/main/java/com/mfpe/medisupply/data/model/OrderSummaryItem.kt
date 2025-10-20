package com.mfpe.medisupply.data.model

data class OrderSummaryItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int
)