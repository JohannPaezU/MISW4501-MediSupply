package com.mfpe.medisupply.data.model

import java.io.Serializable

data class OrderListResponse(
    val orders: List<Order>
): Serializable

