package com.mfpe.medisupply.data.model

import java.io.Serializable

data class SellerHomeResponse (
    val id: Int,
    val numberClients: Int,
    val numberOrders: Int,
    val vendorZone: String
) : Serializable