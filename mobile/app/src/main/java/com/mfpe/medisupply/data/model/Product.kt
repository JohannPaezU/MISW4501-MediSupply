package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class Product(
    val id: String,
    val name: String,
    val details: String,
    val store: String,
    val lote: String,
    val imageUrl: String,
    val dueDate: Date,
    val stock: Int,
    val pricePerUnite: Double,
    val providerId: Int,
    val providerName: String,
    val createdAt: Date
): Serializable
