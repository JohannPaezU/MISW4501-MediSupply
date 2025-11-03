package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class RegisterVisitResponse (
    val id: String,
    val expectedDate: Date,
    val visitDate: Date,
    val observations: String,
    val visualEvidenceUrl: String,
    val status: String,
    val expectedGeoLocation: VisitGeolocation,
    val reportGeoLocation: VisitGeolocation,
    val clientId: VisitClient
) : Serializable
