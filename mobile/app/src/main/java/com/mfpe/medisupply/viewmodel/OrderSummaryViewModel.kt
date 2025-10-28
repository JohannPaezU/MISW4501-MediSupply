package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.data.model.CreateOrderRequest
import com.mfpe.medisupply.data.model.CreateOrderResponse
import com.mfpe.medisupply.data.model.DistributionCenter
import com.mfpe.medisupply.data.model.OrderProductRequest
import com.mfpe.medisupply.data.model.OrderSummaryItem
import com.mfpe.medisupply.data.model.Product
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderSummaryViewModel : ViewModel() {

    private val clientViewModel = ClientViewModel()
    private val distributionCenterViewModel = DistributionCenterViewModel()
    private val ordersViewModel = OrdersViewModel()

    // LiveData for UI state
    private val _clients = MutableLiveData<List<Client>>()
    val clients: LiveData<List<Client>> = _clients

    private val _distributionCenters = MutableLiveData<List<DistributionCenter>>()
    val distributionCenters: LiveData<List<DistributionCenter>> = _distributionCenters

    private val _orderSummaryItems = MutableLiveData<List<OrderSummaryItem>>()
    val orderSummaryItems: LiveData<List<OrderSummaryItem>> = _orderSummaryItems

    private val _totalValue = MutableLiveData<Double>()
    val totalValue: LiveData<Double> = _totalValue

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _orderCreated = MutableLiveData<Boolean>()
    val orderCreated: LiveData<Boolean> = _orderCreated

    // Selected values
    private var selectedClient: Client? = null
    private var selectedCenter: DistributionCenter? = null
    private var selectedDeliveryDate: Calendar? = null

    fun loadClients(authToken: String) {
        clientViewModel.getClients(authToken) { success, message, response ->
            if (success && response != null) {
                _clients.value = response.clients
            } else {
                _errorMessage.value = "Error al cargar clientes: $message"
            }
        }
    }

    fun loadDistributionCenters(authToken: String) {
        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, response ->
            if (success && response != null) {
                _distributionCenters.value = response.distribution_centers
            } else {
                _errorMessage.value = "Error al cargar centros: $message"
            }
        }
    }

    fun calculateOrderSummary(products: List<Product>, quantities: Map<String, Int>) {
        val summaryItems = mutableListOf<OrderSummaryItem>()
        var totalValue = 0.0

        products.forEach { product ->
            val quantity = quantities[product.id] ?: 0
            if (quantity > 0) {
                val itemTotal = product.price_per_unit * quantity
                totalValue += itemTotal
                summaryItems.add(
                    OrderSummaryItem(
                        id = product.id,
                        name = product.name,
                        imageUrl = product.image_url,
                        price = itemTotal,
                        quantity = quantity
                    )
                )
            }
        }

        _orderSummaryItems.value = summaryItems
        _totalValue.value = totalValue
    }

    fun setSelectedClient(client: Client?) {
        selectedClient = client
    }

    fun setSelectedCenter(center: DistributionCenter?) {
        selectedCenter = center
    }

    fun setSelectedDeliveryDate(date: Calendar?) {
        selectedDeliveryDate = date
    }

    fun validateOrder(userRole: String, products: List<Product>, quantities: Map<String, Int>): String? {
        // Validate client selection for commercial users
        if (userRole.lowercase() == "commercial" && selectedClient == null) {
            return "Por favor seleccione un cliente"
        }

        // Validate distribution center
        if (selectedCenter == null) {
            return "Por favor seleccione un centro de distribución"
        }

        // Validate delivery date
        if (selectedDeliveryDate == null) {
            return "Por favor seleccione una fecha de entrega"
        }

        // Validate products
        if (products.isEmpty() || quantities.values.all { it <= 0 }) {
            return "Debe seleccionar al menos un producto"
        }

        return null
    }

    fun createOrder(
        authToken: String,
        userRole: String,
        comments: String?,
        products: List<Product>,
        quantities: Map<String, Int>
    ) {
        _isLoading.value = true

        // Prepare delivery date in ISO format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val deliveryDate = selectedDeliveryDate?.let { dateFormat.format(it.time) } ?: ""

        // Prepare products
        val orderProducts = mutableListOf<OrderProductRequest>()
        products.forEach { product ->
            val quantity = quantities[product.id] ?: 0
            if (quantity > 0) {
                orderProducts.add(
                    OrderProductRequest(
                        product_id = product.id,
                        quantity = quantity
                    )
                )
            }
        }

        // Create the request
        val orderRequest = CreateOrderRequest(
            comments = comments,
            delivery_date = deliveryDate,
            distribution_center_id = selectedCenter?.id ?: "",
            client_id = if (userRole.lowercase() == "commercial") selectedClient?.id else null,
            products = orderProducts
        )

        // Call the API
        ordersViewModel.createOrder(authToken, orderRequest) { success, message, response ->
            _isLoading.value = false
            
            if (success && response != null) {
                _orderCreated.value = true
            } else {
                _errorMessage.value = "Error al crear el pedido: $message"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearOrderCreated() {
        _orderCreated.value = false
    }
}
