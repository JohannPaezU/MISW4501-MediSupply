package com.mfpe.medisupply.data.model

import java.io.Serializable

data class OTPUser (
    val id: String,
    val fullName: String,
    val email: String,
    val role: String
) : Serializable
