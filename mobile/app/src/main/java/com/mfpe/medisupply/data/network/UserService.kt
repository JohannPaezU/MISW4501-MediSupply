package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.LoginUserRequest
import com.mfpe.medisupply.data.model.LoginUserResponse
import com.mfpe.medisupply.data.model.RegisterUserRequest
import com.mfpe.medisupply.data.model.RegisterUserResponse
import com.mfpe.medisupply.data.model.ValidateOTPRequest
import com.mfpe.medisupply.data.model.ValidateOTPResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("auth/register")
    fun registerUser(
        @Body registerUserRequest: RegisterUserRequest
    ): Call<RegisterUserResponse>

    @POST("auth/login")
    fun loginUser(
        @Body loginUserRequest: LoginUserRequest
    ): Call<LoginUserResponse>

    @POST("auth/verify-otp")
    fun validateOTP(
        @Body validateOTPRequest: ValidateOTPRequest
    ): Call<ValidateOTPResponse>

}