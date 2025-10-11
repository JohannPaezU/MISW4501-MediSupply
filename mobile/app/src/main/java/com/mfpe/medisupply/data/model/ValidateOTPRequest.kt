package com.mfpe.medisupply.data.model

import java.io.Serializable

data class ValidateOTPRequest (
    val otp: String
) : Serializable
