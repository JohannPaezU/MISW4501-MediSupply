package com.mfpe.medisupply.data.model

import java.io.Serializable

data class SellerVisitResponse(
    val totalCount: Int,
    val visits: List<Visit>
) : Serializable
