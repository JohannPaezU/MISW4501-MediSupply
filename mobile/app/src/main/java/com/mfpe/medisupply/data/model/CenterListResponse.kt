package com.mfpe.medisupply.data.model

import java.io.Serializable

data class CenterListResponse(
    val total_count: Int,
    val distribution_centers: List<DistributionCenter>
): Serializable
