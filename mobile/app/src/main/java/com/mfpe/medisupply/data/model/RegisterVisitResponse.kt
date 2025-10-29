package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class RegisterVisitResponse (
    val id: String,
    val clientId: String,
    val expectedDate: Date,
    val visitDate: Date,
    val observations: String,
    val visualEvidence: String,
    val geolocation: String,
    val status: String
) : Serializable
