package com.mfpe.medisupply.ui.comercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mfpe.medisupply.databinding.FragmentComClientesBinding

class ComClientesFragment : Fragment() {

    private var _binding: FragmentComClientesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComClientesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textClientes.text = "Clientes"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

