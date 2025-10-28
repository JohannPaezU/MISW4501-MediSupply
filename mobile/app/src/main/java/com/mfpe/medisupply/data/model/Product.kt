package com.mfpe.medisupply.data.model

import java.io.Serializable

data class Product(
    val id: String,
    val name: String,
    val details: String,
    val store: String,
    val batch: String,
    val image_url: String,
    val due_date: String,
    val stock: Int,
    val price_per_unit: Double,
    val created_at: String
): Serializable
