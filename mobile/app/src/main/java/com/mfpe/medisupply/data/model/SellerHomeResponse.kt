package com.mfpe.medisupply.data.model

import java.io.Serializable

data class SellerHomeResponse (
    val id: String,
    val clientsCount: Int,
    val ordersCount: Int,
    val zone: String
) : Serializable