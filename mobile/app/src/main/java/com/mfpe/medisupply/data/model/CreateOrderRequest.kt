package com.mfpe.medisupply.data.model

import java.io.Serializable

data class CreateOrderRequest(
    val comments: String?,
    val delivery_date: String,
    val distribution_center_id: String,
    val client_id: String?,
    val products: List<OrderProductRequest>
) : Serializable

