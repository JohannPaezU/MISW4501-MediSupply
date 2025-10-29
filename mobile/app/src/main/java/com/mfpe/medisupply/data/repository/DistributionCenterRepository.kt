package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.CenterListResponse
import com.mfpe.medisupply.data.network.DistributionCenterService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import retrofit2.Call

class DistributionCenterRepository {
    private val distributionCenterService: DistributionCenterService by lazy {
        RetrofitApiClient.createRetrofitService(DistributionCenterService::class.java)
    }

    fun getDistributionCenters(authToken: String): Call<CenterListResponse> {
        return distributionCenterService.getDistributionCenters(authToken)
    }

}