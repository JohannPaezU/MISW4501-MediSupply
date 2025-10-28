package com.mfpe.medisupply.data.model

import java.io.Serializable

data class OrderProductRequest(
    val product_id: String,
    val quantity: Int
) : Serializable


