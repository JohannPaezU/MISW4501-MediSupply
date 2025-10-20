package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class Order(
    val id: Int,
    val createdAt: Date,
    val deliveryDate: Date,
    val distributionCenterId: String,
    val distributionCenterName: String,
    val comments: String,
    val clientId: Int,
    val sellerId: Int,
    val status: String,
    val products: List<OrderProduct>
): Serializable
