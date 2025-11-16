package com.mfpe.medisupply.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.LoginUserRequest
import com.mfpe.medisupply.data.model.LoginUserResponse
import com.mfpe.medisupply.data.model.RegisterUserRequest
import com.mfpe.medisupply.data.model.RegisterUserResponse
import com.mfpe.medisupply.data.model.ValidateOTPRequest
import com.mfpe.medisupply.data.model.ValidateOTPResponse
import com.mfpe.medisupply.data.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    fun registerUser(registerUserRequest: RegisterUserRequest, onResult: (Boolean, String) -> Unit) {
        userRepository.registerUser(registerUserRequest).enqueue(object : Callback<RegisterUserResponse>{
            override fun onResponse(call: Call<RegisterUserResponse>, res: Response<RegisterUserResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, res.body()?.id!!)
                } else {
                    onResult(false, "Error registering user: ${res.code()}")
                }
            }

            override fun onFailure(call: Call<RegisterUserResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}")
            }
        })
    }

    fun loginUser(loginUserRequest: LoginUserRequest, onResult: (Boolean, String) -> Unit) {
        userRepository.loginUser(loginUserRequest).enqueue(object : Callback<LoginUserResponse>{
            override fun onResponse(call: Call<LoginUserResponse>, res: Response<LoginUserResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, res.body()?.message!!)
                } else {
                    onResult(false, "Error logging in: ${res.code()}")
                }
            }

            override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
                Log.e("UserViewModel", "Login request failed: ${t.message}")
                onResult(false, "Connection error: ${t.message}")
            }
        })
    }

    fun validateOTP(validateOTPRequest: ValidateOTPRequest, onResult: (Boolean, String, ValidateOTPResponse?) -> Unit) {
        userRepository.validateOTP(validateOTPRequest).enqueue(object : Callback<ValidateOTPResponse>{
            override fun onResponse(call: Call<ValidateOTPResponse>, res: Response<ValidateOTPResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "OTP validated.", res.body())
                } else {
                    onResult(false, "Error validating OTP: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<ValidateOTPResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }
}