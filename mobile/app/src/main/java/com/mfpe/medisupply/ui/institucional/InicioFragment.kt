package com.mfpe.medisupply.ui.institucional

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mfpe.medisupply.databinding.FragmentInicioBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.InicioViewModel
import java.util.Calendar

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var inicioViewModel: InicioViewModel
    private lateinit var prefsManager: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        prefsManager = PrefsManager.getInstance(requireContext())
        inicioViewModel = ViewModelProvider(this)[InicioViewModel::class.java]

        val userFullName = prefsManager.getUserFullName ?: "Usuario"
        val firstName = userFullName.split(" ").firstOrNull() ?: "Usuario"
        binding.textInicio.text = "¡Bienvenido, $firstName!"

        setupScheduleVisitButton()

        return root
    }

    private fun setupScheduleVisitButton() {
        binding.btnScheduleVisit.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                
                val (isValid, result) = inicioViewModel.validateAndFormatDate(selectedDate)
                
                if (isValid) {
                    scheduleVisit(result)
                } else {
                    Toast.makeText(
                        requireContext(),
                        result,
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            year,
            month,
            day
        )

        // Usar el ViewModel para obtener las fechas límite
        datePickerDialog.datePicker.minDate = inicioViewModel.getMinDate()
        datePickerDialog.datePicker.maxDate = inicioViewModel.getMaxDate()

        datePickerDialog.show()
    }

    private fun scheduleVisit(isoDate: String) {
        val authToken = prefsManager.getAuthToken
        if (authToken.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: Token de autenticación no encontrado",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        inicioViewModel.scheduleVisit(authToken, isoDate) { success, message ->
            Toast.makeText(
                requireContext(),
                message,
                if (success) Toast.LENGTH_LONG else Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
