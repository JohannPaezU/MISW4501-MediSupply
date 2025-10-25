package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.RegisterVisitRequest
import com.mfpe.medisupply.data.model.RegisterVisitResponse
import com.mfpe.medisupply.data.network.RetrofitApiClient
import com.mfpe.medisupply.data.network.VisitService
import retrofit2.Call

class VisitRepository {
    private val visitService: VisitService by lazy {
        RetrofitApiClient.createRetrofitService(VisitService::class.java)
    }

    fun registerCompletedVisit(authToken: String, id: String, request: RegisterVisitRequest): Call<RegisterVisitResponse> {
        return visitService.registerCompletedVisit(authToken, id, request)
    }

}