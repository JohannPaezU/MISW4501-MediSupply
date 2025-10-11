package com.mfpe.medisupply.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mfpe.medisupply.adapters.OrderProductsAdapter
import com.mfpe.medisupply.databinding.ActivityOrderBinding
import com.mfpe.medisupply.viewmodel.ProductsViewModel

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderProductsAdapter: OrderProductsAdapter
    private lateinit var productsViewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        loadProducts()
    }

    private fun setupToolbar() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val quantities = orderProductsAdapter.getProductsWithQuantities()
            // TODO: Implementar lógica de guardado
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderProductsAdapter = OrderProductsAdapter()

        binding.rvOrderProducts.apply {
            layoutManager = GridLayoutManager(this@OrderActivity, 2)
            adapter = orderProductsAdapter
        }
    }

    private fun loadProducts() {
        productsViewModel.getProducts() { success, message, response ->
            if (success && response != null) {
                orderProductsAdapter.submitList(response.products)
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}

