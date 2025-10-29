package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.CenterListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface DistributionCenterService {

    @GET("distribution-centers")
    fun getDistributionCenters(
        @Header("Authorization") authToken: String,
    ): Call<CenterListResponse>

}