package com.mfpe.medisupply.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mfpe.medisupply.data.model.Order
import com.mfpe.medisupply.databinding.ActivityOrderDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding

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
}
