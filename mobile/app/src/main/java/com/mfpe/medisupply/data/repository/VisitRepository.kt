package com.mfpe.medisupply.data.repository

import com.mfpe.medisupply.data.model.RegisterVisitRequest
import com.mfpe.medisupply.data.model.RegisterVisitResponse
import com.mfpe.medisupply.data.network.RetrofitApiClient
import com.mfpe.medisupply.data.network.VisitService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Locale

class VisitRepository {
    private val visitService: VisitService by lazy {
        RetrofitApiClient.createRetrofitService(VisitService::class.java)
    }

    fun registerCompletedVisit(authToken: String, id: String, request: RegisterVisitRequest): Call<RegisterVisitResponse> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val visitDateString = dateFormat.format(request.visitDate)

        // Crear RequestBody para cada campo
        val textPlain = MediaType.parse("text/plain")
        val visitDateBody = RequestBody.create(textPlain, visitDateString)
        val observationsBody = RequestBody.create(textPlain, request.observations)
        val latitudeBody = RequestBody.create(textPlain, request.latitude.toString())
        val longitudeBody = RequestBody.create(textPlain, request.longitude.toString())

        return visitService.registerCompletedVisit(
            authToken = authToken,
            id = id,
            visitDate = visitDateBody,
            observations = observationsBody,
            latitude = latitudeBody,
            longitude = longitudeBody
        )
    }

}