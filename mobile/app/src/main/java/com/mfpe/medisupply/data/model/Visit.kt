package com.mfpe.medisupply.data.model

import java.io.Serializable

data class Visit(
    val id: String,
    val expectedDate: String,
    val visitDate: String?,
    val observations: String,
    val visualEvidenceUrl: String,
    val status: String,
    val expectedGeolocation: VisitGeolocation,
    val reportGeolocation: VisitGeolocation,
    val client: VisitClient
) : Serializable

