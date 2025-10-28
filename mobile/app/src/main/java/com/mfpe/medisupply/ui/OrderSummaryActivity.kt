package com.mfpe.medisupply.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mfpe.medisupply.R
import com.mfpe.medisupply.adapters.OrderSummaryAdapter
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.data.model.DistributionCenter
import com.mfpe.medisupply.data.model.OrderSummaryItem
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.databinding.ActivityOrderSummaryBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.ClientViewModel
import com.mfpe.medisupply.viewmodel.DistributionCenterViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var distributionCenterViewModel: DistributionCenterViewModel
    private lateinit var prefsManager: PrefsManager
    private var selectedDeliveryDate: Calendar? = null
    private var products: List<Product> = emptyList()
    private var quantities: Map<String, Int> = emptyMap()
    private var clientsList: List<Client> = emptyList()
    private var centersList: List<DistributionCenter> = emptyList()
    private var selectedClient: Client? = null
    private var selectedCenter: DistributionCenter? = null
    private var selectedDistributionCenter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel y PrefsManager
        clientViewModel = ClientViewModel()
        distributionCenterViewModel = DistributionCenterViewModel()
        prefsManager = PrefsManager.getInstance(this)

        // Obtener datos del intent
        @Suppress("DEPRECATION")
        products = intent.getSerializableExtra("products") as? List<Product> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        quantities = intent.getSerializableExtra("quantities") as? Map<String, Int> ?: emptyMap()

        setupToolbar()
        setupRecyclerView()
        setupClientVisibility()
        loadCenters()
        setupClientDropdown()
        setupDistributionCenterDropdown()
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

    private fun setupClientDropdown() {
        binding.inputClient.setOnItemClickListener { parent, _, position, _ ->
            selectedClient = clientsList[position]
        }
    }

    private fun setupClientVisibility() {
        val userRole = prefsManager.getUserRole?.lowercase()
        if (userRole == "commercial") {
            binding.inputClientLayout.visibility = View.VISIBLE
            loadClients()
        } else {
            binding.inputClientLayout.visibility = View.GONE
        }
    }

    private fun setupDistributionCenterDropdown() {
        binding.inputDistributionCenter.setOnItemClickListener { _, _, position, _ ->
            selectedCenter = centersList[position]
        }
    }

    private fun loadClients() {
        val authToken = prefsManager.getAuthToken ?: ""
        val sellerId = prefsManager.getuserId

        clientViewModel.getClients(authToken, sellerId) { success, message, response ->
            if (success && response != null) {
                clientsList = response.clients
                val clientNames = clientsList.map { it.fullName }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clientNames)
                binding.inputClient.setAdapter(adapter)
            } else {
                Toast.makeText(this, "Error al cargar clientes: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCenters() {
        val authToken = prefsManager.getAuthToken ?: ""

        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, response ->
            if (success && response != null) {
                centersList = response.centers
                val centerNames = centersList.map { it.description }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, centerNames)
                binding.inputDistributionCenter.setAdapter(adapter)
            } else {
                Toast.makeText(this, "Error al cargar centros: $message", Toast.LENGTH_SHORT).show()
            }
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
        if (binding.inputClientLayout.visibility == View.VISIBLE && selectedClient == null) {
            Toast.makeText(this, "Por favor seleccione un cliente", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedDistributionCenter == null) {
            Toast.makeText(this, "Por favor seleccione un centro de distribución", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedDeliveryDate == null) {
            Toast.makeText(
                this,
                getString(R.string.error_select_delivery_date),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun createOrder() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar pedido")
            .setMessage("¿Estás seguro que deseas confirmar este pedido?")
            .setPositiveButton("Confirmar") { _, _ ->
                Toast.makeText(this, "Pedido creado exitosamente.", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
