package com.mfpe.medisupply.ui.comercial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mfpe.medisupply.adapters.ProductsAdapter
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.databinding.ActivityRecommendedProductsBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.RecommendedProductsViewModel

class RecommendedProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendedProductsBinding
    private lateinit var adapter: ProductsAdapter
    private lateinit var viewModel: RecommendedProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecommendedProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[RecommendedProductsViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        loadRecommendedProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductsAdapter()

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@RecommendedProductsActivity, 2)
            adapter = this@RecommendedProductsActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadRecommendedProducts() {
        val clientId = intent.getStringExtra(EXTRA_CLIENT_ID)
        
        if (clientId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: ID de cliente no proporcionado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.progressBar.visibility = ProgressBar.VISIBLE

        val authToken = PrefsManager.getInstance(this).getAuthToken ?: ""
        viewModel.getRecommendedProducts(authToken, clientId) { success, message, products ->
            binding.progressBar.visibility = ProgressBar.GONE

            if (success && products != null) {
                adapter.submitList(products)
                
                if (products.isEmpty()) {
                    Toast.makeText(this@RecommendedProductsActivity, 
                        "No hay productos recomendados disponibles", 
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@RecommendedProductsActivity, 
                    message, 
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val EXTRA_CLIENT_ID = "extra_client_id"
        
        fun start(context: Context, clientId: String) {
            val intent = Intent(context, RecommendedProductsActivity::class.java)
            intent.putExtra(EXTRA_CLIENT_ID, clientId)
            context.startActivity(intent)
        }
    }
}
