package com.mfpe.medisupply.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.Order
import com.mfpe.medisupply.data.model.OrderProduct
import com.mfpe.medisupply.databinding.ActivityOrderDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private var isProductsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val order = intent.getSerializableExtra("ORDER") as? Order

        order?.let {
            displayOrderDetails(it)
            setupProductsButton(it)
        }
    }

    private fun displayOrderDetails(order: Order) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.textOrderName.text = "Orden #${order.id}"
        binding.textOrderStatus.text = order.status
        binding.textCreatedDate.text = dateFormat.format(order.createdAt)
        binding.textDeliveryDate.text = dateFormat.format(order.deliveryDate)
        binding.textDistributionCenter.text = order.distributionCenterName
        binding.textComments.text = order.comments.ifEmpty { "Sin comentarios" }
    }

    private fun setupProductsButton(order: Order) {
        binding.btnViewProducts.setOnClickListener {
            isProductsVisible = !isProductsVisible

            if (isProductsVisible) {
                displayProducts(order.products)
                binding.cardProducts.visibility = View.VISIBLE
                binding.btnViewProducts.text = "Ocultar productos"
            } else {
                binding.cardProducts.visibility = View.GONE
                binding.btnViewProducts.text = "Ver productos"
            }
        }
    }

    private fun displayProducts(products: List<OrderProduct>) {
        binding.productsContainer.removeAllViews()

        products.forEachIndexed { index, product ->
            val productView = layoutInflater.inflate(R.layout.item_product_order, binding.productsContainer, false)

            val productNameTextView = productView.findViewById<TextView>(R.id.textProductName)
            val productQuantityTextView = productView.findViewById<TextView>(R.id.textProductQuantity)

            productNameTextView.text = product.productName
            productQuantityTextView.text = "Cantidad: ${product.quantity}"

            binding.productsContainer.addView(productView)

            if (index < products.size - 1) {
                val separator = View(this)
                separator.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(R.dimen.separator_height)
                )
                separator.setBackgroundColor(resources.getColor(R.color.gray, null))
                binding.productsContainer.addView(separator)
            }
        }
    }
}
