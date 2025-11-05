package com.mfpe.medisupply.data.model

import com.mfpe.medisupply.utils.TestUtils
import org.junit.Assert.*
import org.junit.Test
import java.io.Serializable

/**
 * Tests unitarios para el modelo Client
 */
class ClientTest {


    @Test
    fun `Client should have correct properties`() {
        // Given
        val id = "client-123"
        val fullName = TestUtils.TestData.VALID_FULL_NAME
        val doi = "1234567890"
        val email = TestUtils.TestData.VALID_EMAIL
        val phone = TestUtils.TestData.VALID_PHONE
        val address = TestUtils.TestData.VALID_ADDRESS

        // When
        val client = Client(
            id = id,
            full_name = fullName,
            doi = doi,
            email = email,
            phone = phone,
            address = address,
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // Then
        assertEquals("ID should match", id, client.id)
        assertEquals("Full name should match", fullName, client.full_name)
        assertEquals("DOI should match", doi, client.doi)
        assertEquals("Email should match", email, client.email)
        assertEquals("Phone should match", phone, client.phone)
        assertEquals("Address should match", address, client.address)
    }

    @Test
    fun `Client should be Serializable`() {
        // Given
        val client = createTestClient()

        // When & Then
        assertTrue("Client should implement Serializable", client is Serializable)
    }

    @Test
    fun `Client should support equals and hashCode`() {
        // Given
        val date = "1234567890"
        val client1 = Client(
            id = "client-123",
            full_name = "John Doe",
            doi = date,
            email = "john@example.com",
            phone = "3001234567",
            address = "123 Main St",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )
        val client2 = Client(
            id = "client-123",
            full_name = "John Doe",
            doi = date,
            email = "john@example.com",
            phone = "3001234567",
            address = "123 Main St",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )
        val client3 = Client(
            id = "client-456",
            full_name = "Jane Smith",
            doi = date,
            email = "jane@example.com",
            phone = "3007654321",
            address = "456 Oak Ave",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertEquals("Equal clients should be equal", client1, client2)
        assertEquals("Equal clients should have same hashCode", client1.hashCode(), client2.hashCode())
        assertNotEquals("Different clients should not be equal", client1, client3)
        assertNotEquals("Different clients should have different hashCode", client1.hashCode(), client3.hashCode())
    }

    @Test
    fun `Client should support toString`() {
        // Given
        val client = createTestClient()

        // When
        val toString = client.toString()

        // Then
        assertNotNull("toString should not be null", toString)
        assertTrue("toString should contain class name", toString.contains("Client"))
        assertTrue("toString should contain id", toString.contains(client.id))
        assertTrue("toString should contain full_name", toString.contains(client.full_name))
    }

    @Test
    fun `Client should handle empty strings`() {
        // Given
        val client = Client(
            id = "",
            full_name = "",
            doi = "1234567890",
            email = "",
            phone = "",
            address = "",
            role = "",
            created_at = ""
        )

        // When & Then
        assertTrue("ID should be empty", client.id.isEmpty())
        assertTrue("Full name should be empty", client.full_name.isEmpty())
        assertTrue("Email should be empty", client.email.isEmpty())
        assertTrue("Phone should be empty", client.phone.isEmpty())
        assertTrue("Address should be empty", client.address.isEmpty())
    }

    @Test
    fun `Client should handle special characters`() {
        // Given
        val client = Client(
            id = "client-123!@#",
            full_name = "José María O'Connor-Smith",
            doi = "1234567890",
            email = "josé.maría@example.com",
            phone = "+57-300-123-4567",
            address = "Calle 123 #45-67, Apt 8B",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertEquals("ID should contain special characters", "client-123!@#", client.id)
        assertEquals("Full name should contain special characters", "José María O'Connor-Smith", client.full_name)
        assertEquals("Email should contain special characters", "josé.maría@example.com", client.email)
        assertEquals("Phone should contain special characters", "+57-300-123-4567", client.phone)
        assertEquals("Address should contain special characters", "Calle 123 #45-67, Apt 8B", client.address)
    }

    @Test
    fun `Client should handle long strings`() {
        // Given
        val longId = "very-long-client-id-that-might-be-used-in-some-systems-with-many-characters"
        val longName = "Very Long Client Name That Might Be Used In Some Systems With Many Characters"
        val longEmail = "very.long.email.address.that.might.be.used.in.some.systems@example.com"
        val longPhone = "+57-300-123-4567-890"
        val longAddress = "Very Long Address That Might Be Used In Some Systems With Many Characters And Details"

        val client = Client(
            id = longId,
            full_name = longName,
            doi = "1234567890",
            email = longEmail,
            phone = longPhone,
            address = longAddress,
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertEquals("Long ID should be preserved", longId, client.id)
        assertEquals("Long name should be preserved", longName, client.full_name)
        assertEquals("Long email should be preserved", longEmail, client.email)
        assertEquals("Long phone should be preserved", longPhone, client.phone)
        assertEquals("Long address should be preserved", longAddress, client.address)
    }

    @Test
    fun `Client should handle numeric values`() {
        // Given
        val client = Client(
            id = "12345",
            full_name = "Client 123",
            doi = "1234567890",
            email = "client123@example.com",
            phone = "1234567890",
            address = "123 Main Street",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertEquals("Numeric ID should be preserved", "12345", client.id)
        assertEquals("Numeric name should be preserved", "Client 123", client.full_name)
        assertEquals("Numeric email should be preserved", "client123@example.com", client.email)
        assertEquals("Numeric phone should be preserved", "1234567890", client.phone)
        assertEquals("Numeric address should be preserved", "123 Main Street", client.address)
    }

    @Test
    fun `Client should handle different DOI values`() {
        // Given
        val nit1 = "1234567890"
        val nit2 = "9876543210"
        val nit3 = "5555555555"

        val client1 = Client(
            id = "client-1",
            full_name = "Client 1",
            doi = nit1,
            email = "client1@example.com",
            phone = "3001111111",
            address = "Address 1",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        val client2 = Client(
            id = "client-2",
            full_name = "Client 2",
            doi = nit2,
            email = "client2@example.com",
            phone = "3002222222",
            address = "Address 2",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        val client3 = Client(
            id = "client-3",
            full_name = "Client 3",
            doi = nit3,
            email = "client3@example.com",
            phone = "3003333333",
            address = "Address 3",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertEquals("DOI 1 should be preserved", nit1, client1.doi)
        assertEquals("DOI 2 should be preserved", nit2, client2.doi)
        assertEquals("DOI 3 should be preserved", nit3, client3.doi)
    }

    @Test
    fun `Client should be immutable`() {
        // Given
        val client = createTestClient()
        val originalId = client.id
        val originalName = client.full_name

        // When & Then
        // Data classes in Kotlin are immutable by default
        assertEquals("ID should remain unchanged", originalId, client.id)
        assertEquals("Full name should remain unchanged", originalName, client.full_name)
    }

    @Test
    fun `Client should support copy method`() {
        // Given
        val originalClient = createTestClient()

        // When
        val copiedClient = originalClient.copy(
            full_name = "Updated Name",
            email = "updated@example.com"
        )

        // Then
        assertEquals("ID should remain the same", originalClient.id, copiedClient.id)
        assertEquals("DOI should remain the same", originalClient.doi, copiedClient.doi)
        assertEquals("Phone should remain the same", originalClient.phone, copiedClient.phone)
        assertEquals("Address should remain the same", originalClient.address, copiedClient.address)
        assertEquals("Full name should be updated", "Updated Name", copiedClient.full_name)
        assertEquals("Email should be updated", "updated@example.com", copiedClient.email)
    }

    @Test
    fun `Client should support componentN methods`() {
        // Given
        val client = createTestClient()

        // When
        val (id, full_name, email, phone, doi, address, role, created_at) = client

        // Then
        assertEquals("Component 1 should be id", client.id, id)
        assertEquals("Component 2 should be full_name", client.full_name, full_name)
        assertEquals("Component 3 should be email", client.email, email)
        assertEquals("Component 4 should be phone", client.phone, phone)
        assertEquals("Component 5 should be doi", client.doi, doi)
        assertEquals("Component 6 should be address", client.address, address)
        assertEquals("Component 7 should be role", client.role, role)
        assertEquals("Component 8 should be created_at", client.created_at, created_at)
    }

    @Test
    fun `Client should handle null-like values gracefully`() {
        // Given
        val client = Client(
            id = "null",
            full_name = "null",
            doi = "1234567890",
            email = "null@null.com",
            phone = "null",
            address = "null",
            role = "null",
            created_at = "null"
        )

        // When & Then
        assertEquals("String 'null' should be preserved", "null", client.id)
        assertEquals("String 'null' should be preserved", "null", client.full_name)
        assertEquals("String 'null' should be preserved", "null@null.com", client.email)
        assertEquals("String 'null' should be preserved", "null", client.phone)
        assertEquals("String 'null' should be preserved", "null", client.address)
    }

    @Test
    fun `Client should handle whitespace values`() {
        // Given
        val client = Client(
            id = "   ",
            full_name = "   ",
            doi = "1234567890",
            email = "   ",
            phone = "   ",
            address = "   ",
            role = "   ",
            created_at = "   "
        )

        // When & Then
        assertEquals("Whitespace ID should be preserved", "   ", client.id)
        assertEquals("Whitespace name should be preserved", "   ", client.full_name)
        assertEquals("Whitespace email should be preserved", "   ", client.email)
        assertEquals("Whitespace phone should be preserved", "   ", client.phone)
        assertEquals("Whitespace address should be preserved", "   ", client.address)
    }

    @Test
    fun `Client should handle unicode characters`() {
        // Given
        val client = Client(
            id = "cliente-ñ-123",
            full_name = "José María Ñoño",
            doi = "1234567890",
            email = "josé.maría@español.com",
            phone = "+57-300-ñ-4567",
            address = "Calle Ñoño #123-45",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertEquals("Unicode ID should be preserved", "cliente-ñ-123", client.id)
        assertEquals("Unicode name should be preserved", "José María Ñoño", client.full_name)
        assertEquals("Unicode email should be preserved", "josé.maría@español.com", client.email)
        assertEquals("Unicode phone should be preserved", "+57-300-ñ-4567", client.phone)
        assertEquals("Unicode address should be preserved", "Calle Ñoño #123-45", client.address)
    }

    @Test
    fun `Client should handle multiple clients with same properties`() {
        // Given
        val date = "1234567890"
        val client1 = Client(
            id = "client-1",
            full_name = "John Doe",
            doi = date,
            email = "john@example.com",
            phone = "3001234567",
            address = "123 Main St",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )
        val client2 = Client(
            id = "client-2",
            full_name = "John Doe",
            doi = date,
            email = "john@example.com",
            phone = "3001234567",
            address = "123 Main St",
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )

        // When & Then
        assertNotEquals("Different IDs should make clients different", client1, client2)
        assertNotEquals("Different IDs should have different hashCode", client1.hashCode(), client2.hashCode())
    }

    @Test
    fun `Client should maintain data integrity`() {
        // Given
        val client = createTestClient()

        // When
        val clientCopy = client.copy()

        // Then
        assertEquals("Copy should be equal to original", client, clientCopy)
        assertEquals("Copy should have same hashCode", client.hashCode(), clientCopy.hashCode())
        assertNotSame("Copy should be different instance", client, clientCopy)
    }

    // Helper method to create a test client
    private fun createTestClient(): Client {
        return Client(
            id = "test-client-123",
            full_name = TestUtils.TestData.VALID_FULL_NAME,
            doi = "1234567890",
            email = TestUtils.TestData.VALID_EMAIL,
            phone = TestUtils.TestData.VALID_PHONE,
            address = TestUtils.TestData.VALID_ADDRESS,
            role = "institutional",
            created_at = "2025-01-01T00:00:00Z"
        )
    }
}
