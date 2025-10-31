package com.mfpe.medisupply.data.network

import com.mfpe.medisupply.data.model.RegisterVisitResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface VisitService {

    @Multipart
    @PATCH("visits/{id}/report")
    fun registerCompletedVisit(
        @Header("Authorization") authToken: String,
        @Path("id") id: String,
        @Part("visit_date") visitDate: RequestBody,
        @Part("observations") observations: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody
    ): Call<RegisterVisitResponse>

}