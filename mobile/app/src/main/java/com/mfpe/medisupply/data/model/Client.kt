package com.mfpe.medisupply.data.model

import java.io.Serializable

data class Client(
    val id: String,
    val full_name: String,
    val email: String,
    val phone: String,
    val doi: String,
    val address: String,
    val role: String,
    val created_at: String
): Serializable
