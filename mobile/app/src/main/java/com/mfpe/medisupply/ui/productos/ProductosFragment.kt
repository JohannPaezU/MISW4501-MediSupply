package com.mfpe.medisupply.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mfpe.medisupply.databinding.FragmentProductosBinding
import com.mfpe.medisupply.viewmodel.ProductosViewModel

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val productosViewModel = ViewModelProvider(this).get(ProductosViewModel::class.java)
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textProductos
        productosViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

