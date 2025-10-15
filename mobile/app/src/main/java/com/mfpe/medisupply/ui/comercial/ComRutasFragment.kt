package com.mfpe.medisupply.ui.comercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mfpe.medisupply.databinding.FragmentComRutasBinding

class ComRutasFragment : Fragment() {

    private var _binding: FragmentComRutasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComRutasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textRutas.text = "Rutas"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

