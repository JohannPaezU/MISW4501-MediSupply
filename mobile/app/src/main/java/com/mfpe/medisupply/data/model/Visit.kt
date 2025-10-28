package com.mfpe.medisupply.data.model

import java.io.Serializable

data class Visit(
    val id: String,
    val expectedDate: String,
    val visitDate: String?,
    val observations: String,
    val visualEvidence: String,
    val visitGeolocation: String,
    val status: String,
    val client: VisitClient
) : Serializable

