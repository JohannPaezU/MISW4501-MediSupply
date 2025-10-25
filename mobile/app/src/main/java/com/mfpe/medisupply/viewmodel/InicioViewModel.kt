package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.VisitRequest
import com.mfpe.medisupply.data.model.VisitResponse
import com.mfpe.medisupply.data.repository.ClientRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InicioViewModel(
    private val clientRepository: ClientRepository = ClientRepository()
) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Inicio"
    }
    val text: LiveData<String> = _text

    fun scheduleVisit(authToken: String, expectedDate: String, onResult: (Boolean, String) -> Unit) {
        val visitRequest = VisitRequest(expectedDate)
        clientRepository.createVisit(authToken, visitRequest).enqueue(object : Callback<VisitResponse> {
            override fun onResponse(call: Call<VisitResponse>, response: Response<VisitResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(true, "Visita agendada exitosamente para el ${expectedDate.substring(0, 10)}")
                } else {
                    onResult(false, "Error al agendar la visita: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<VisitResponse>, t: Throwable) {
                onResult(false, "Error de conexión: ${t.message}")
            }
        })
    }

    fun validateAndFormatDate(selectedDate: Calendar): Pair<Boolean, String> {
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)
        
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DAY_OF_MONTH, 30)

        return if (selectedDate.after(today) && (selectedDate.before(maxDate) || selectedDate.equals(maxDate))) {
            val isoDate = formatToIso8601(selectedDate)
            Pair(true, isoDate)
        } else {
            Pair(false, "La fecha debe ser entre mañana y los próximos 30 días")
        }
    }

    private fun formatToIso8601(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'10:00:00'Z'", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun getMinDate(): Long {
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)
        return tomorrow.timeInMillis
    }

    fun getMaxDate(): Long {
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DAY_OF_MONTH, 30)
        return maxDate.timeInMillis
    }
}

