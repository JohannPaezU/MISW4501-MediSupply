package com.mfpe.medisupply.ui.institucional

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mfpe.medisupply.adapters.ProductsAdapter
import com.mfpe.medisupply.databinding.FragmentProductosBinding
import com.mfpe.medisupply.viewmodel.ProductsViewModel

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var productsViewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        setupRecyclerView()
        loadProducts()

        return root
    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter()

        binding.rvProductos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productsAdapter
        }
    }

    private fun loadProducts() {
        productsViewModel.getProducts() { success, message, response ->
            if (success && response != null) {
                productsAdapter.submitList(response.products)
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
