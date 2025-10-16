package com.mfpe.medisupply.data.model

import java.io.Serializable

data class RegisterUserRequest (
    val fullName: String,
    val email: String,
    val role: String,
    val password: String,
    val phone: String,
    val doi: String,
    val address: String
) : Serializable
