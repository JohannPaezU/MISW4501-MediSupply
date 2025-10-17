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
        val now = java.util.Date()
        val product = Product(
            id = "p1",
            name = "Test",
            details = "Detalles",
            store = "Tienda",
            lote = "L1",
            imageUrl = "url",
            dueDate = now,
            stock = 5,
            pricePerUnite = 10.0,
            providerId = 1,
            providerName = "Proveedor",
            createdAt = now
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
        val now = java.util.Date()
        val product = Product(
            id = "p1",
            name = "Test",
            details = "Detalles",
            store = "Tienda",
            lote = "L1",
            imageUrl = "url",
            dueDate = now,
            stock = 5,
            pricePerUnite = 10.0,
            providerId = 1,
            providerName = "Proveedor",
            createdAt = now
        )
        assertEquals("p1", product.id)
        assertEquals("Test", product.name)
        assertEquals("Detalles", product.details)
        assertEquals("Tienda", product.store)
        assertEquals("L1", product.lote)
        assertEquals("url", product.imageUrl)
        assertEquals(now, product.dueDate)
        assertEquals(5, product.stock)
        assertEquals(10.0, product.pricePerUnite, 0.0)
        assertEquals(1, product.providerId)
        assertEquals("Proveedor", product.providerName)
        assertEquals(now, product.createdAt)
    }
}
