package com.mfpe.medisupply.data.model

import java.io.Serializable

data class VisitGeolocation (
    val id: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Serializable