package com.mfpe.medisupply.ui

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mfpe.medisupply.data.model.Order
import com.mfpe.medisupply.data.model.OrderDetailResponse
import com.mfpe.medisupply.databinding.ActivityOrderDetailBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.OrderDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var orderDetailViewModel: OrderDetailViewModel
    private var orderDetail: OrderDetailResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        orderDetailViewModel = ViewModelProvider(this)[OrderDetailViewModel::class.java]

        setupClickListeners()

        // Obtener orderId del intent o del Order (para compatibilidad)
        val orderId = intent.getStringExtra("ORDER_ID")
        @Suppress("DEPRECATION")
        val order = intent.getSerializableExtra("ORDER") as? Order

        when {
            !orderId.isNullOrEmpty() -> {
                // Cargar desde API
                loadOrderDetail(orderId)
            }
            order != null -> {
                // Modo compatibilidad: mostrar datos del Order pasado
                displayOrderDetailsFromOrder(order)
            }
            else -> {
                Toast.makeText(this, "No se pudo obtener la información de la orden", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnViewProducts.setOnClickListener {
            orderDetail?.let {
                OrderProductsActivity.start(this, it.products)
            } ?: run {
                Toast.makeText(this, "No hay productos disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadOrderDetail(orderId: String) {
        binding.progressBar.visibility = ProgressBar.VISIBLE

        val authToken = PrefsManager.getInstance(this).getAuthToken ?: ""
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        orderDetailViewModel.getOrderDetail(authToken, orderId) { success, message, response ->
            binding.progressBar.visibility = ProgressBar.GONE

            if (success && response != null) {
                orderDetail = response
                displayOrderDetails(response)
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun displayOrderDetails(orderDetail: OrderDetailResponse) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val apiDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())

        binding.textOrderName.text = "Orden #${orderDetail.id}"
        binding.textOrderStatus.text = "Estado: ${orderDetail.status}"

        // Parsear fechas
        try {
            val deliveryDate = apiDateFormat.parse(orderDetail.delivery_date)
            deliveryDate?.let {
                binding.textDeliveryDate.text = dateFormat.format(it)
            } ?: run {
                binding.textDeliveryDate.text = orderDetail.delivery_date
            }
        } catch (e: Exception) {
            binding.textDeliveryDate.text = orderDetail.delivery_date
        }

        try {
            val createdAt = apiDateTimeFormat.parse(orderDetail.created_at)
            createdAt?.let {
                binding.textCreatedDate.text = dateFormat.format(it)
            } ?: run {
                binding.textCreatedDate.text = orderDetail.created_at
            }
        } catch (e: Exception) {
            binding.textCreatedDate.text = orderDetail.created_at
        }

        binding.textDistributionCenter.text = orderDetail.distribution_center.name
        binding.textComments.text = orderDetail.comments ?: "Sin comentarios"
        
        // Calcular y mostrar el monto total de la orden
        val totalAmount = calculateTotalAmount(orderDetail.products)
        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        binding.textTotalAmount.text = formato.format(totalAmount)
    }
    
    private fun calculateTotalAmount(products: List<com.mfpe.medisupply.data.model.OrderProductDetail>): Double {
        return products.sumOf { product ->
            product.price_per_unit * product.quantity
        }
    }

    private fun displayOrderDetailsFromOrder(order: Order) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.textOrderName.text = "Orden #${order.id}"
        binding.textOrderStatus.text = "Estado: ${order.status}"
        binding.textCreatedDate.text = dateFormat.format(order.createdAt)
        binding.textDeliveryDate.text = dateFormat.format(order.deliveryDate)
        binding.textDistributionCenter.text = order.distributionCenterName
        binding.textComments.text = order.comments.ifEmpty { "Sin comentarios" }

        // Para compatibilidad: cargar productos desde el Order
        // Pero preferiblemente cargar desde API
        val orderId = order.id
        loadOrderDetail(orderId)
    }
}
