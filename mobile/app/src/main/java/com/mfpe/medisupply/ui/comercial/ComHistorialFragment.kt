package com.mfpe.medisupply.ui.comercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mfpe.medisupply.databinding.FragmentComHistorialBinding

class ComHistorialFragment : Fragment() {

    private var _binding: FragmentComHistorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComHistorialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textHistorial.text = "Historial"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

