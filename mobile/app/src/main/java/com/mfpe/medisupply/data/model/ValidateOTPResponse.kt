package com.mfpe.medisupply.data.model

import java.io.Serializable

data class ValidateOTPResponse (
    val id: Int,
    val token: String,
    val fullName: String,
    val email: String,
    val role: String
) : Serializable