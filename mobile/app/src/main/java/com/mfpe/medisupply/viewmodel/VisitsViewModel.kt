package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.RegisterVisitRequest
import com.mfpe.medisupply.data.model.RegisterVisitResponse
import com.mfpe.medisupply.data.repository.VisitRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitsViewModel(
    private val visitRepository: VisitRepository = VisitRepository()
) : ViewModel() {

    fun registerCompletedVisit(authToken:String, id: String, request: RegisterVisitRequest,
                               onResult: (Boolean, String, RegisterVisitResponse?) -> Unit) {
        visitRepository.registerCompletedVisit(authToken, id, request).enqueue(object :
            Callback<RegisterVisitResponse> {
            override fun onResponse(call: Call<RegisterVisitResponse>, res: Response<RegisterVisitResponse>) {
                if (res.isSuccessful && res.body() != null) {
                    onResult(true, "Visit registered.", res.body())
                } else {
                    onResult(false, "Error registering visit: ${res.code()}", null)
                }
            }

            override fun onFailure(call: Call<RegisterVisitResponse>, t: Throwable) {
                onResult(false, "Connection error: ${t.message}", null)
            }
        })
    }

}
