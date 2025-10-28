package com.mfpe.medisupply.data.model

import java.io.Serializable

data class CenterListResponse(
    val centers: List<DistributionCenter>
): Serializable
