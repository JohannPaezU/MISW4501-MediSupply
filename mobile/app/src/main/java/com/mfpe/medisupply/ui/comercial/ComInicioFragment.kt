package com.mfpe.medisupply.ui.comercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mfpe.medisupply.databinding.FragmentComInicioBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.SellerViewModel

class ComInicioFragment : Fragment() {

    private var _binding: FragmentComInicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var sellerViewModel: SellerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComInicioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sellerViewModel = ViewModelProvider(this)[SellerViewModel::class.java]

        val prefsManager = PrefsManager.getInstance(requireContext())
        val userFullName = prefsManager.getUserFullName ?: "Usuario"
        val firstName = userFullName.split(" ").firstOrNull() ?: "Usuario"
        binding.textInicio.text = "¡Bienvenido, $firstName!"

        loadHomeData()

        return root
    }

    private fun loadHomeData() {
        sellerViewModel.getHome { success, message, data ->
            if (success && data != null) {
                binding.textNumClientes.text = data.numberClients.toString()
                binding.textNumOrdenes.text = data.numberOrders.toString()
                binding.textZona.text = data.vendorZone
            } else {
                Toast.makeText(requireContext(), "Error al cargar datos: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
