package com.mfpe.medisupply.ui.comercial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mfpe.medisupply.adapters.ProductsAdapter
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.data.network.ClientService
import com.mfpe.medisupply.data.network.RetrofitApiClient
import com.mfpe.medisupply.databinding.ActivityRecommendedProductsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendedProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendedProductsBinding
    private lateinit var adapter: ProductsAdapter
    private var clientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendedProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID del cliente desde el Intent
        clientId = intent.getStringExtra("client_id")

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
        if (clientId == null) {
            Toast.makeText(this, "Error: ID de cliente no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = ProgressBar.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val clientService = RetrofitApiClient.createRetrofitService(ClientService::class.java)
                val response = clientService.getRecommendedProducts(
                    clientId!!,
                    "Bearer test"
                ).execute()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = ProgressBar.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val products = response.body()!!.products
                        adapter.submitList(products)
                        
                        if (products.isEmpty()) {
                            Toast.makeText(this@RecommendedProductsActivity, 
                                "No hay productos recomendados disponibles", 
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RecommendedProductsActivity, 
                            "Error al cargar productos recomendados: ${response.message()}", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@RecommendedProductsActivity, 
                        "Error de conexión: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        fun start(context: Context, clientId: String) {
            val intent = Intent(context, RecommendedProductsActivity::class.java)
            intent.putExtra("client_id", clientId)
            context.startActivity(intent)
        }
    }
}
