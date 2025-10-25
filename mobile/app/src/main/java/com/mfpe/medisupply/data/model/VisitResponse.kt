package com.mfpe.medisupply.data.model

import java.io.Serializable

data class VisitResponse(
    val id: String,
    val expectedDate: String,
    val status: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
): Serializable
