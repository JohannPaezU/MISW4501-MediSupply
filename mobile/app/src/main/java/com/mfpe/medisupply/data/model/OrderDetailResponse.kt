package com.mfpe.medisupply.data.model

import java.io.Serializable

data class OrderDetailResponse(
    val id: String,
    val comments: String?,
    val delivery_date: String,
    val status: String,
    val created_at: String,
    val client: Client,
    val distribution_center: DistributionCenter,
    val products: List<OrderProductDetail>
): Serializable

