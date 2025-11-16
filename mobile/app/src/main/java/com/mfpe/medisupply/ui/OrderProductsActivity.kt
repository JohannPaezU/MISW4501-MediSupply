package com.mfpe.medisupply.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mfpe.medisupply.adapters.OrderProductsAdapter
import com.mfpe.medisupply.data.model.OrderProductDetail
import com.mfpe.medisupply.databinding.ActivityOrderProductsBinding

class OrderProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderProductsBinding
    private lateinit var adapter: OrderProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = OrderProductsAdapter()

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@OrderProductsActivity, 2)
            adapter = this@OrderProductsActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProducts() {
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        val products = intent.getSerializableExtra(EXTRA_PRODUCTS) as? ArrayList<OrderProductDetail>
        
        if (products != null) {
            adapter.submitList(products)
        }
    }

    companion object {
        private const val EXTRA_PRODUCTS = "extra_products"
        
        fun start(context: Context, products: List<OrderProductDetail>) {
            val intent = Intent(context, OrderProductsActivity::class.java)
            intent.putExtra(EXTRA_PRODUCTS, ArrayList(products))
            context.startActivity(intent)
        }
    }
}

