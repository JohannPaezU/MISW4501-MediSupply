package com.mfpe.medisupply.data.model

import java.io.Serializable

data class DistributionCenter(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val country: String,
    val created_at: String
): Serializable
