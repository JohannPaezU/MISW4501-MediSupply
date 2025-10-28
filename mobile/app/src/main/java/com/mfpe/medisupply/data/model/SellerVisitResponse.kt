package com.mfpe.medisupply.data.model

import java.io.Serializable

data class SellerVisitResponse(
    val sellerId: Int,
    val date: String,
    val totalVisits: Int,
    val visits: List<Visit>
) : Serializable
