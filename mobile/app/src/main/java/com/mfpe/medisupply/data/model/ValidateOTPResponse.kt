package com.mfpe.medisupply.data.model

import java.io.Serializable

data class ValidateOTPResponse (
    val accessToken: String,
    val user: OTPUser
) : Serializable


