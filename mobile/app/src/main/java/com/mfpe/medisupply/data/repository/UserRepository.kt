package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.LoginUserRequest
import com.mfpe.medisupply.data.model.LoginUserResponse
import com.mfpe.medisupply.data.model.RegisterUserRequest
import com.mfpe.medisupply.data.model.RegisterUserResponse
import com.mfpe.medisupply.data.model.ValidateOTPRequest
import com.mfpe.medisupply.data.model.ValidateOTPResponse
import com.mfpe.medisupply.data.network.RetrofitApiClient
import com.mfpe.medisupply.data.network.UserService
import retrofit2.Call

class UserRepository {
    private val userService: UserService by lazy {
        RetrofitApiClient.createRetrofitService(UserService::class.java)
    }

    fun registerUser(registerUserRequest: RegisterUserRequest): Call<RegisterUserResponse> {
        return userService.registerUser(registerUserRequest)
    }

    fun loginUser(loginUserRequest: LoginUserRequest): Call<LoginUserResponse> {
        return userService.loginUser(loginUserRequest)
    }

    fun validateOTP(validateOTPRequest: ValidateOTPRequest): Call<ValidateOTPResponse> {
        return userService.validateOTP(validateOTPRequest)
    }

}