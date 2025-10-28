package com.mfpe.medisupply.data.model

import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

/**
 * Tests unitarios para los modelos de datos
 */
class DataModelTest {

    @Test
    fun `LoginUserRequest should have correct properties`() {
        // Given
        val email = TestUtils.TestData.VALID_EMAIL
        val password = TestUtils.TestData.VALID_PASSWORD

        // When
        val loginRequest = LoginUserRequest(email = email, password = password)

        // Then
        assertEquals("Email should match", email, loginRequest.email)
        assertEquals("Password should match", password, loginRequest.password)
    }

    @Test
    fun `LoginUserRequest with empty email should be invalid`() {
        // Given
        val loginRequest = LoginUserRequest(email = "", password = TestUtils.TestData.VALID_PASSWORD)

        // When & Then
        assertTrue("Email should be empty", loginRequest.email.isEmpty())
    }

    @Test
    fun `LoginUserRequest with empty password should be invalid`() {
        // Given
        val loginRequest = LoginUserRequest(email = TestUtils.TestData.VALID_EMAIL, password = "")

        // When & Then
        assertTrue("Password should be empty", loginRequest.password.isEmpty())
    }

    @Test
    fun `RegisterUserRequest should have correct properties`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = TestUtils.TestData.VALID_FULL_NAME,
            email = TestUtils.TestData.VALID_EMAIL,
            role = "institutional",
            password = TestUtils.TestData.VALID_PASSWORD,
            phone = TestUtils.TestData.VALID_PHONE,
            doi = TestUtils.TestData.VALID_NIT,
            address = TestUtils.TestData.VALID_ADDRESS
        )

        // When & Then
        assertEquals("Full name should match", TestUtils.TestData.VALID_FULL_NAME, registerRequest.fullName)
        assertEquals("Email should match", TestUtils.TestData.VALID_EMAIL, registerRequest.email)
        assertEquals("Role should be institutional", "institutional", registerRequest.role)
        assertEquals("Password should match", TestUtils.TestData.VALID_PASSWORD, registerRequest.password)
        assertEquals("Phone should match", TestUtils.TestData.VALID_PHONE, registerRequest.phone)
        assertEquals("NIT should match", TestUtils.TestData.VALID_NIT, registerRequest.doi)
        assertEquals("Address should match", TestUtils.TestData.VALID_ADDRESS, registerRequest.address)
    }

    @Test
    fun `RegisterUserRequest with empty fields should be invalid`() {
        // Given
        val registerRequest = RegisterUserRequest(
            fullName = "",
            email = "",
            role = "",
            password = "",
            phone = "",
            doi = "",
            address = ""
        )

        // When & Then
        assertTrue("Full name should be empty", registerRequest.fullName.isEmpty())
        assertTrue("Email should be empty", registerRequest.email.isEmpty())
        assertTrue("Role should be empty", registerRequest.role.isEmpty())
        assertTrue("Password should be empty", registerRequest.password.isEmpty())
        assertTrue("Phone should be empty", registerRequest.phone.isEmpty())
        assertTrue("NIT should be empty", registerRequest.doi.isEmpty())
        assertTrue("Address should be empty", registerRequest.address.isEmpty())
    }

    @Test
    fun `ValidateOTPRequest should have correct properties`() {
        // Given
        val otp = TestUtils.TestData.VALID_OTP

        // When
        val otpRequest = ValidateOTPRequest(otpCode = otp, email = TestUtils.TestData.VALID_EMAIL)

        // Then
        assertEquals("OTP should match", otp, otpRequest.otpCode)
    }

    @Test
    fun `ValidateOTPRequest with empty otp should be invalid`() {
        // Given
        val otpRequest = ValidateOTPRequest(otpCode = "", email = TestUtils.TestData.VALID_EMAIL)

        // When & Then
        assertTrue("OTP should be empty", otpRequest.otpCode.isEmpty())
    }

    @Test
    fun `LoginUserResponse should have correct properties`() {
        // Given
        val message = "Login successful"

        // When
        val loginResponse = LoginUserResponse(message = message)

        // Then
        assertEquals("Message should match", message, loginResponse.message)
    }

    @Test
    fun `RegisterUserResponse should have correct properties`() {
        // Given
        val message = "Registration successful"

        // When
        val registerResponse = RegisterUserResponse(id = message, createdAt = Date())

        // Then
        assertEquals("Message should match", message, registerResponse.id)
    }

    @Test
    fun `ValidateOTPResponse should have correct properties`() {
        // Given
        val token = TestUtils.TestData.VALID_TOKEN
        val fullName = TestUtils.TestData.VALID_FULL_NAME
        val email = TestUtils.TestData.VALID_EMAIL
        val role = TestUtils.TestData.VALID_ROLE

        // When
        val otpResponse = ValidateOTPResponse(
            accessToken = token,
            user = OTPUser(
                id = "id",
                fullName = fullName,
                email = email,
                role = role)
        )

        // Then
        assertEquals("Token should match", token, otpResponse.accessToken)
        assertEquals("Full name should match", fullName, otpResponse.user.fullName)
        assertEquals("Email should match", email, otpResponse.user.email)
        assertEquals("Role should match", role, otpResponse.user.role)
    }

    @Test
    fun `data classes should be immutable`() {
        // Given
        val loginRequest = LoginUserRequest(
            email = TestUtils.TestData.VALID_EMAIL,
            password = TestUtils.TestData.VALID_PASSWORD
        )

        // When & Then
        // Los data classes en Kotlin son inmutables por defecto
        assertNotNull("LoginRequest should not be null", loginRequest)
        assertEquals("Email should remain unchanged", TestUtils.TestData.VALID_EMAIL, loginRequest.email)
        assertEquals("Password should remain unchanged", TestUtils.TestData.VALID_PASSWORD, loginRequest.password)
    }

    @Test
    fun `Order should have correct properties`() {
        val now = java.util.Date()
        val orderProduct = OrderProduct(productId = "p1", productName = "Producto 1", quantity = 2)
        val order = Order(
            id = 1,
            createdAt = now,
            deliveryDate = now,
            distributionCenterId = "dc1",
            distributionCenterName = "Centro 1",
            comments = "Sin comentarios",
            clientId = 10,
            sellerId = 20,
            status = "pending",
            products = listOf(orderProduct)
        )
        assertEquals(1, order.id)
        assertEquals(now, order.createdAt)
        assertEquals(now, order.deliveryDate)
        assertEquals("dc1", order.distributionCenterId)
        assertEquals("Centro 1", order.distributionCenterName)
        assertEquals("Sin comentarios", order.comments)
        assertEquals(10, order.clientId)
        assertEquals(20, order.sellerId)
        assertEquals("pending", order.status)
        assertEquals(listOf(orderProduct), order.products)
    }

    @Test
    fun `OrderProduct should have correct properties`() {
        val orderProduct = OrderProduct(productId = "p1", productName = "Producto 1", quantity = 5)
        assertEquals("p1", orderProduct.productId)
        assertEquals("Producto 1", orderProduct.productName)
        assertEquals(5, orderProduct.quantity)
    }

    @Test
    fun `ProductListResponse should have correct properties`() {
        val product = Product(
            id = "p1",
            name = "Test",
            details = "Detalles",
            store = "Tienda",
            batch = "L1",
            image_url = "url",
            due_date = "2026-09-24",
            stock = 5,
            price_per_unit = 10.0,
            created_at = "2025-10-23T05:44:07.144451Z"
        )
        val products = listOf(product)
        val response = ProductListResponse(products = products)
        assertEquals(products, response.products)
    }

    @Test
    fun `OrderListResponse should have correct properties`() {
        val now = java.util.Date()
        val orderProduct = OrderProduct(productId = "p1", productName = "Producto 1", quantity = 2)
        val order = Order(
            id = 1,
            createdAt = now,
            deliveryDate = now,
            distributionCenterId = "dc1",
            distributionCenterName = "Centro 1",
            comments = "Sin comentarios",
            clientId = 10,
            sellerId = 20,
            status = "pending",
            products = listOf(orderProduct)
        )
        val orders = listOf(order)
        val response = OrderListResponse(orders = orders)
        assertEquals(orders, response.orders)
    }

    @Test
    fun `OrderSummaryItem should have correct properties`() {
        val item = OrderSummaryItem(id = "1", name = "Test", imageUrl = "img", price = 10.0, quantity = 2)
        assertEquals("1", item.id)
        assertEquals("Test", item.name)
        assertEquals("img", item.imageUrl)
        assertEquals(10.0, item.price, 0.0)
        assertEquals(2, item.quantity)
    }

    @Test
    fun `SellerHomeResponse should have correct properties`() {
        val response = SellerHomeResponse(id = 1, numberClients = 5, numberOrders = 10, vendorZone = "Zona 1")
        assertEquals(1, response.id)
        assertEquals(5, response.numberClients)
        assertEquals(10, response.numberOrders)
        assertEquals("Zona 1", response.vendorZone)
    }

    @Test
    fun `Product should have correct properties`() {
        val product = Product(
            id = "p1",
            name = "Test",
            details = "Detalles",
            store = "Tienda",
            batch = "L1",
            image_url = "url",
            due_date = "2026-09-24",
            stock = 5,
            price_per_unit = 10.0,
            created_at = "2025-10-23T05:44:07.144451Z"
        )
        assertEquals("p1", product.id)
        assertEquals("Test", product.name)
        assertEquals("Detalles", product.details)
        assertEquals("Tienda", product.store)
        assertEquals("L1", product.batch)
        assertEquals("url", product.image_url)
        assertEquals("2026-09-24", product.due_date)
        assertEquals(5, product.stock)
        assertEquals(10.0, product.price_per_unit, 0.0)
        assertEquals("2025-10-23T05:44:07.144451Z", product.created_at)
    }

    @Test
    fun `OrderProductRequest should have correct properties`() {
        // Given
        val productId = "prod123"
        val quantity = 5

        // When
        val orderProductRequest = OrderProductRequest(product_id = productId, quantity = quantity)

        // Then
        assertEquals("Product ID should match", productId, orderProductRequest.product_id)
        assertEquals("Quantity should match", quantity, orderProductRequest.quantity)
    }

    @Test
    fun `OrderProductRequest with zero quantity should be valid`() {
        // Given
        val productId = "prod123"
        val quantity = 0

        // When
        val orderProductRequest = OrderProductRequest(product_id = productId, quantity = quantity)

        // Then
        assertEquals("Product ID should match", productId, orderProductRequest.product_id)
        assertEquals("Quantity should be zero", quantity, orderProductRequest.quantity)
    }

    @Test
    fun `OrderProductRequest with negative quantity should be valid`() {
        // Given
        val productId = "prod123"
        val quantity = -1

        // When
        val orderProductRequest = OrderProductRequest(product_id = productId, quantity = quantity)

        // Then
        assertEquals("Product ID should match", productId, orderProductRequest.product_id)
        assertEquals("Quantity should be negative", quantity, orderProductRequest.quantity)
    }

    @Test
    fun `OrderProductRequest with empty product_id should be valid`() {
        // Given
        val productId = ""
        val quantity = 5

        // When
        val orderProductRequest = OrderProductRequest(product_id = productId, quantity = quantity)

        // Then
        assertTrue("Product ID should be empty", orderProductRequest.product_id.isEmpty())
        assertEquals("Quantity should match", quantity, orderProductRequest.quantity)
    }

    @Test
    fun `CreateOrderRequest should have correct properties`() {
        // Given
        val comments = "Test comments"
        val deliveryDate = "2025-12-31"
        val distributionCenterId = "dc123"
        val clientId = "client456"
        val products = listOf(
            OrderProductRequest(product_id = "prod1", quantity = 2),
            OrderProductRequest(product_id = "prod2", quantity = 3)
        )

        // When
        val createOrderRequest = CreateOrderRequest(
            comments = comments,
            delivery_date = deliveryDate,
            distribution_center_id = distributionCenterId,
            client_id = clientId,
            products = products
        )

        // Then
        assertEquals("Comments should match", comments, createOrderRequest.comments)
        assertEquals("Delivery date should match", deliveryDate, createOrderRequest.delivery_date)
        assertEquals("Distribution center ID should match", distributionCenterId, createOrderRequest.distribution_center_id)
        assertEquals("Client ID should match", clientId, createOrderRequest.client_id)
        assertEquals("Products should match", products, createOrderRequest.products)
        assertEquals("Products size should be 2", 2, createOrderRequest.products.size)
    }

    @Test
    fun `CreateOrderRequest with null comments should be valid`() {
        // Given
        val deliveryDate = "2025-12-31"
        val distributionCenterId = "dc123"
        val clientId = "client456"
        val products = listOf(OrderProductRequest(product_id = "prod1", quantity = 2))

        // When
        val createOrderRequest = CreateOrderRequest(
            comments = null,
            delivery_date = deliveryDate,
            distribution_center_id = distributionCenterId,
            client_id = clientId,
            products = products
        )

        // Then
        assertNull("Comments should be null", createOrderRequest.comments)
        assertEquals("Delivery date should match", deliveryDate, createOrderRequest.delivery_date)
        assertEquals("Distribution center ID should match", distributionCenterId, createOrderRequest.distribution_center_id)
        assertEquals("Client ID should match", clientId, createOrderRequest.client_id)
    }

    @Test
    fun `CreateOrderRequest with null client_id should be valid`() {
        // Given
        val comments = "Test comments"
        val deliveryDate = "2025-12-31"
        val distributionCenterId = "dc123"
        val products = listOf(OrderProductRequest(product_id = "prod1", quantity = 2))

        // When
        val createOrderRequest = CreateOrderRequest(
            comments = comments,
            delivery_date = deliveryDate,
            distribution_center_id = distributionCenterId,
            client_id = null,
            products = products
        )

        // Then
        assertEquals("Comments should match", comments, createOrderRequest.comments)
        assertNull("Client ID should be null", createOrderRequest.client_id)
        assertEquals("Distribution center ID should match", distributionCenterId, createOrderRequest.distribution_center_id)
    }

    @Test
    fun `CreateOrderRequest with empty products list should be valid`() {
        // Given
        val comments = "Test comments"
        val deliveryDate = "2025-12-31"
        val distributionCenterId = "dc123"

        // When
        val createOrderRequest = CreateOrderRequest(
            comments = comments,
            delivery_date = deliveryDate,
            distribution_center_id = distributionCenterId,
            client_id = "client456",
            products = emptyList()
        )

        // Then
        assertTrue("Products list should be empty", createOrderRequest.products.isEmpty())
    }

    @Test
    fun `CreateOrderResponse should have correct properties`() {
        // Given
        val id = "order123"
        val createdAt = "2025-01-01T10:00:00Z"
        val deliveryDate = "2025-12-31T10:00:00Z"
        val distributionCenterId = "dc123"
        val distributionCenterName = "Centro Distribución Norte"
        val comments = "Test comments"
        val clientId = "client456"
        val sellerId = "seller789"
        val status = "pending"
        val products = listOf(
            OrderProduct(productId = "prod1", productName = "Producto 1", quantity = 2),
            OrderProduct(productId = "prod2", productName = "Producto 2", quantity = 3)
        )

        // When
        val createOrderResponse = CreateOrderResponse(
            id = id,
            createdAt = createdAt,
            deliveryDate = deliveryDate,
            distributionCenterId = distributionCenterId,
            distributionCenterName = distributionCenterName,
            comments = comments,
            clientId = clientId,
            sellerId = sellerId,
            status = status,
            products = products
        )

        // Then
        assertEquals("ID should match", id, createOrderResponse.id)
        assertEquals("Created at should match", createdAt, createOrderResponse.createdAt)
        assertEquals("Delivery date should match", deliveryDate, createOrderResponse.deliveryDate)
        assertEquals("Distribution center ID should match", distributionCenterId, createOrderResponse.distributionCenterId)
        assertEquals("Distribution center name should match", distributionCenterName, createOrderResponse.distributionCenterName)
        assertEquals("Comments should match", comments, createOrderResponse.comments)
        assertEquals("Client ID should match", clientId, createOrderResponse.clientId)
        assertEquals("Seller ID should match", sellerId, createOrderResponse.sellerId)
        assertEquals("Status should match", status, createOrderResponse.status)
        assertEquals("Products should match", products, createOrderResponse.products)
        assertEquals("Products size should be 2", 2, createOrderResponse.products.size)
    }

    @Test
    fun `CreateOrderResponse with null comments should be valid`() {
        // Given
        val id = "order123"
        val createdAt = "2025-01-01T10:00:00Z"
        val deliveryDate = "2025-12-31T10:00:00Z"
        val distributionCenterId = "dc123"
        val distributionCenterName = "Centro Distribución Norte"
        val clientId = "client456"
        val sellerId = "seller789"
        val status = "pending"
        val products = listOf(OrderProduct(productId = "prod1", productName = "Producto 1", quantity = 2))

        // When
        val createOrderResponse = CreateOrderResponse(
            id = id,
            createdAt = createdAt,
            deliveryDate = deliveryDate,
            distributionCenterId = distributionCenterId,
            distributionCenterName = distributionCenterName,
            comments = null,
            clientId = clientId,
            sellerId = sellerId,
            status = status,
            products = products
        )

        // Then
        assertNull("Comments should be null", createOrderResponse.comments)
        assertEquals("ID should match", id, createOrderResponse.id)
        assertEquals("Status should match", status, createOrderResponse.status)
    }

    @Test
    fun `CreateOrderResponse with null client_id should be valid`() {
        // Given
        val id = "order123"
        val createdAt = "2025-01-01T10:00:00Z"
        val deliveryDate = "2025-12-31T10:00:00Z"
        val distributionCenterId = "dc123"
        val distributionCenterName = "Centro Distribución Norte"
        val comments = "Test comments"
        val sellerId = "seller789"
        val status = "pending"
        val products = listOf(OrderProduct(productId = "prod1", productName = "Producto 1", quantity = 2))

        // When
        val createOrderResponse = CreateOrderResponse(
            id = id,
            createdAt = createdAt,
            deliveryDate = deliveryDate,
            distributionCenterId = distributionCenterId,
            distributionCenterName = distributionCenterName,
            comments = comments,
            clientId = null,
            sellerId = sellerId,
            status = status,
            products = products
        )

        // Then
        assertNull("Client ID should be null", createOrderResponse.clientId)
        assertEquals("ID should match", id, createOrderResponse.id)
        assertEquals("Status should match", status, createOrderResponse.status)
    }

    @Test
    fun `CreateOrderResponse with empty products list should be valid`() {
        // Given
        val id = "order123"
        val createdAt = "2025-01-01T10:00:00Z"
        val deliveryDate = "2025-12-31T10:00:00Z"
        val distributionCenterId = "dc123"
        val distributionCenterName = "Centro Distribución Norte"
        val sellerId = "seller789"
        val status = "pending"

        // When
        val createOrderResponse = CreateOrderResponse(
            id = id,
            createdAt = createdAt,
            deliveryDate = deliveryDate,
            distributionCenterId = distributionCenterId,
            distributionCenterName = distributionCenterName,
            comments = null,
            clientId = null,
            sellerId = sellerId,
            status = status,
            products = emptyList()
        )

        // Then
        assertTrue("Products list should be empty", createOrderResponse.products.isEmpty())
        assertEquals("ID should match", id, createOrderResponse.id)
        assertEquals("Status should match", status, createOrderResponse.status)
    }

    @Test
    fun `CreateOrderResponse with different statuses should be valid`() {
        // Given
        val statuses = listOf("pending", "confirmed", "shipped", "delivered", "cancelled")
        val baseResponse = CreateOrderResponse(
            id = "order123",
            createdAt = "2025-01-01T10:00:00Z",
            deliveryDate = "2025-12-31T10:00:00Z",
            distributionCenterId = "dc123",
            distributionCenterName = "Centro",
            comments = null,
            clientId = null,
            sellerId = "seller789",
            status = "",
            products = emptyList()
        )

        // When & Then
        statuses.forEach { status ->
            val response = baseResponse.copy(status = status)
            assertEquals("Status should be $status", status, response.status)
        }
    }
}
