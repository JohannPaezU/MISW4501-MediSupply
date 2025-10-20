package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class RegisterUserResponse (
    val id: String,
    val createdAt: Date
) : Serializable