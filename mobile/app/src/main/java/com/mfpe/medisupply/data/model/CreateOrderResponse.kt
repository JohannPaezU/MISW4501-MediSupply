package com.mfpe.medisupply.data.model

import java.io.Serializable

data class CreateOrderResponse(
    val id: String,
    val createdAt: String,
    val deliveryDate: String,
    val distributionCenterId: String,
    val distributionCenterName: String,
    val comments: String?,
    val clientId: String?,
    val sellerId: String,
    val status: String,
    val products: List<OrderProduct>
) : Serializable

