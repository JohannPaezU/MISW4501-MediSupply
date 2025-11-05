package com.mfpe.medisupply.data.model

import java.io.Serializable

data class OrderProductDetail(
    val id: String,
    val name: String,
    val store: String,
    val batch: String,
    val due_date: String,
    val price_per_unit: Double,
    val quantity: Int
): Serializable

