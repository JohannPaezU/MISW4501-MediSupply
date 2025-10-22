package com.mfpe.medisupply.data.model

import java.io.Serializable
import java.util.Date

data class Client(
    val id: String,
    val fullName: String,
    val doi: Date,
    val email: String,
    val phone: String,
    val address: String
): Serializable
