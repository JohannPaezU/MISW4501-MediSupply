package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.RegisterVisitRequest
import com.mfpe.medisupply.data.model.RegisterVisitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface VisitService {

    @POST("visits/{id}/report")
    fun registerCompletedVisit(
        @Header("Authorization") authToken: String,
        @Path("id") id: String,
        @Body registerVisitRequest: RegisterVisitRequest
    ): Call<RegisterVisitResponse>

}