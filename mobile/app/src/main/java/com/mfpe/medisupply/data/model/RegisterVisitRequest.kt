package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class RegisterVisitRequest (
    val visitDate: Date,
    val observations: String,
    val visualEvidence: String,
    val geolocation: String
) : Serializable
