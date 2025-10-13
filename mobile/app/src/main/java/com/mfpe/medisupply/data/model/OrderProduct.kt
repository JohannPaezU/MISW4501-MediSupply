package com.mfpe.medisupply.data.model

import java.io.Serializable

data class OrderProduct(
    val productId: String,
    val productName: String,
    val quantity: Int
): Serializable
