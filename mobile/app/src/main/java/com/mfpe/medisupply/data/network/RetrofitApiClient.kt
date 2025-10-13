package com.mfpe.medisupply.data.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.mfpe.medisupply.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApiClient {

    private fun createRetrofitInstance(): Retrofit {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <S> createRetrofitService(service: Class<S>): S {
        return createRetrofitInstance().create(service)
    }

}