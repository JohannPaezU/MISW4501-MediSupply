package com.mfpe.medisupply.data.model

import java.io.Serializable

data class Client(
    val id: String,
    val fullName: String,
    val doi: String,
    val email: String,
    val phone: String,
    val address: String
): Serializable
