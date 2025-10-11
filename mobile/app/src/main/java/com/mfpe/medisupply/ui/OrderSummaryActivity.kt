package com.mfpe.medisupply.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mfpe.medisupply.R
import com.mfpe.medisupply.adapters.OrderSummaryAdapter
import com.mfpe.medisupply.adapters.OrderSummaryItem
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.databinding.ActivityOrderSummaryBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private var selectedDeliveryDate: Calendar? = null
    private var products: List<Product> = emptyList()
    private var quantities: Map<String, Int> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        @Suppress("DEPRECATION")
        products = intent.getSerializableExtra("products") as? List<Product> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        quantities = intent.getSerializableExtra("quantities") as? Map<String, Int> ?: emptyMap()

        setupToolbar()
        setupRecyclerView()
        setupDeliveryDatePicker()
        setupButtons()
        loadOrderSummary()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderSummaryAdapter = OrderSummaryAdapter()
        binding.rvOrderSummaryProducts.apply {
            layoutManager = LinearLayoutManager(this@OrderSummaryActivity)
            adapter = orderSummaryAdapter
        }
    }

    private fun setupDeliveryDatePicker() {
        binding.inputDeliveryDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = selectedDeliveryDate ?: Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                selectedDeliveryDate = selectedDate
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.inputDeliveryDate.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setupButtons() {
        binding.btnCreateOrder.setOnClickListener {
            if (validateOrder()) {
                createOrder()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadOrderSummary() {
        val summaryItems = mutableListOf<OrderSummaryItem>()
        var totalValue = 0.0

        products.forEach { product ->
            val quantity = quantities[product.id] ?: 0
            if (quantity > 0) {
                val itemTotal = product.pricePerUnite * quantity
                totalValue += itemTotal
                summaryItems.add(
                    OrderSummaryItem(
                        id = product.id,
                        name = product.name,
                        imageUrl = product.imageUrl,
                        price = itemTotal,
                        quantity = quantity
                    )
                )
            }
        }

        orderSummaryAdapter.submitList(summaryItems)
        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        binding.tvTotalLabel.text = getString(R.string.label_total_value, formato.format(totalValue))
    }

    private fun validateOrder(): Boolean {
        if (selectedDeliveryDate == null) {
            Toast.makeText(
                this,
                getString(R.string.error_select_delivery_date),
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    private fun createOrder() {
        Toast.makeText(this, "Pedido creado exitosamente.", Toast.LENGTH_LONG).show()
        setResult(RESULT_OK)
        finish()
    }

}
