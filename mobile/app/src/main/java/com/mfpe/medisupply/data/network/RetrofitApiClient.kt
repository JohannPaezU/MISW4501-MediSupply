package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApiClient {

    private fun createRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <S> createRetrofitService(service: Class<S>): S {
        return createRetrofitInstance().create(service)
    }

}