package com.mfpe.medisupply.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mfpe.medisupply.R
import com.mfpe.medisupply.adapters.OrderProductsAdapter
import com.mfpe.medisupply.databinding.ActivityOrderBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.ProductsViewModel

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderProductsAdapter: OrderProductsAdapter
    private lateinit var productsViewModel: ProductsViewModel

    private val orderSummaryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            finish()
        }
    }

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

            if (quantities.values.all { it == 0 }) {
                Toast.makeText(
                    this,
                    getString(R.string.error_no_products_selected),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val selectedProducts = productsViewModel.getCurrentProducts()
                .filter { product ->
                    val quantity = quantities[product.id] ?: 0
                    quantity > 0
                }

            val intent = Intent(this, OrderSummaryActivity::class.java)
            intent.putExtra("products", ArrayList(selectedProducts))
            intent.putExtra("quantities", HashMap(quantities))
            orderSummaryLauncher.launch(intent)
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
        productsViewModel.getProducts(PrefsManager.getInstance(this).getAuthToken!!) { success, message, response ->
            if (success && response != null) {
                orderProductsAdapter.submitList(response.products)
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
