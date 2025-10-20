package com.mfpe.medisupply.data.model

import java.io.Serializable

data class LoginUserRequest (
    val email: String,
    val password: String
) : Serializable
