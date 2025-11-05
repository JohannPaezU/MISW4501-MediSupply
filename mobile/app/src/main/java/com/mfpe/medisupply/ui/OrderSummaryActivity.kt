package com.mfpe.medisupply.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mfpe.medisupply.R
import com.mfpe.medisupply.adapters.OrderSummaryAdapter
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.data.model.DistributionCenter
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.databinding.ActivityOrderSummaryBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.OrderSummaryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private lateinit var prefsManager: PrefsManager
    private val orderSummaryViewModel: OrderSummaryViewModel by viewModels()
    
    private var products: List<Product> = emptyList()
    private var quantities: Map<String, Int> = emptyMap()
    private var clientsList: List<Client> = emptyList()
    private var centersList: List<DistributionCenter> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar PrefsManager
        prefsManager = PrefsManager.getInstance(this)

        // Obtener datos del intent
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        products = intent.getSerializableExtra("products") as? List<Product> ?: emptyList()
        
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        quantities = intent.getSerializableExtra("quantities") as? Map<String, Int> ?: emptyMap()

        setupToolbar()
        setupRecyclerView()
        setupClientVisibility()
        setupObservers()
        loadData()
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

    private fun setupObservers() {
        orderSummaryViewModel.clients.observe(this, Observer { clients ->
            clientsList = clients
            val clientNames = clients.map { it.full_name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clientNames)
            binding.inputClient.setAdapter(adapter)
        })

        orderSummaryViewModel.distributionCenters.observe(this, Observer { centers ->
            centersList = centers
            val centerNames = centers.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, centerNames)
            binding.inputDistributionCenter.setAdapter(adapter)
        })

        orderSummaryViewModel.orderSummaryItems.observe(this, Observer { items ->
            orderSummaryAdapter.submitList(items)
        })

        orderSummaryViewModel.totalValue.observe(this, Observer { total ->
            val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            binding.tvTotalLabel.text = getString(R.string.label_total_value, formato.format(total))
        })

        orderSummaryViewModel.isLoading.observe(this, Observer { isLoading ->
            binding.btnCreateOrder.isEnabled = !isLoading
            binding.btnCreateOrder.text = if (isLoading) "Creando pedido..." else getString(R.string.do_create_order)
        })

        orderSummaryViewModel.errorMessage.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                orderSummaryViewModel.clearError()
            }
        })

        orderSummaryViewModel.orderCreated.observe(this, Observer { created ->
            if (created) {
                Toast.makeText(this, "Pedido creado exitosamente.", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
                orderSummaryViewModel.clearOrderCreated()
            }
        })
    }

    private fun loadData() {
        val authToken = prefsManager.getAuthToken ?: ""
        orderSummaryViewModel.loadDistributionCenters(authToken)
        
        val userRole = prefsManager.getUserRole?.lowercase()
        if (userRole == "commercial") {
            orderSummaryViewModel.loadClients(authToken)
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
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                orderSummaryViewModel.setSelectedDeliveryDate(selectedDate)
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
        binding.inputClient.setOnItemClickListener { _, _, position, _ ->
            orderSummaryViewModel.setSelectedClient(clientsList[position])
        }
    }

    private fun setupClientVisibility() {
        val userRole = prefsManager.getUserRole?.lowercase()
        if (userRole == "commercial") {
            binding.inputClientLayout.visibility = View.VISIBLE
        } else {
            binding.inputClientLayout.visibility = View.GONE
        }
    }

    private fun setupDistributionCenterDropdown() {
        binding.inputDistributionCenter.setOnItemClickListener { _, _, position, _ ->
            orderSummaryViewModel.setSelectedCenter(centersList[position])
        }
    }

    private fun loadOrderSummary() {
        orderSummaryViewModel.calculateOrderSummary(products, quantities)
    }

    private fun validateOrder(): Boolean {
        val userRole = prefsManager.getUserRole ?: ""
        val errorMessage = orderSummaryViewModel.validateOrder(userRole, products, quantities)
        
        if (errorMessage != null) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }

    private fun createOrder() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar pedido")
            .setMessage("¿Estás seguro que deseas confirmar este pedido?")
            .setPositiveButton("Confirmar") { _, _ ->
                performCreateOrder()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performCreateOrder() {
        val authToken = prefsManager.getAuthToken ?: ""
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Error: Token de autenticación no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val userRole = prefsManager.getUserRole ?: ""
        val comments = binding.inputComments.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
        
        orderSummaryViewModel.createOrder(authToken, userRole, comments, products, quantities)
    }
}
