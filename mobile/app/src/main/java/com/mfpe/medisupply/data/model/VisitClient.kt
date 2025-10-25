package com.mfpe.medisupply.data.model

import java.io.Serializable

data class VisitClient(
    val id: String,
    val name: String,
    val geolocation: String
) : Serializable

