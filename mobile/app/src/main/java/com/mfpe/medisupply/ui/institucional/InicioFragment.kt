package com.mfpe.medisupply.ui.institucional

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mfpe.medisupply.databinding.FragmentInicioBinding
import com.mfpe.medisupply.utils.PrefsManager

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val prefsManager = PrefsManager.getInstance(requireContext())
        val userFullName = prefsManager.getUserFullName ?: "Usuario"
        val firstName = userFullName.split(" ").firstOrNull() ?: "Usuario"
        binding.textInicio.text = "¡Bienvenido, $firstName!"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
