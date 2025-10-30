package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class RegisterVisitRequest (
    val visitDate: Date,
    val observations: String,
    val latitude: Double,
    val longitude: Double,
    val visualEvidence: String,
) : Serializable
