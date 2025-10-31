package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test

class VisitClientTest {

    @Test
    fun `VisitClient should have correct properties`() {
        // Given
        val id = "client-123"
        val fullName = "Test Client"

        // When
        val client = VisitClient(
            id = id,
            fullName = fullName
        )

        // Then
        assertEquals("ID should match", id, client.id)
        assertEquals("Full name should match", fullName, client.fullName)
    }

    @Test
    fun `VisitClient should be Serializable`() {
        // Given
        val client = VisitClient(
            id = "client-123",
            fullName = "Test Client"
        )

        // When & Then
        assertTrue("Should be Serializable", client is java.io.Serializable)
    }

    @Test
    fun `VisitClient should handle empty strings`() {
        // Given & When
        val client = VisitClient(
            id = "",
            fullName = ""
        )

        // Then
        assertTrue("ID should be empty", client.id.isEmpty())
        assertTrue("Full name should be empty", client.fullName.isEmpty())
    }

    @Test
    fun `VisitClient should handle long names`() {
        // Given
        val longName = "A".repeat(500)

        // When
        val client = VisitClient(
            id = "client-123",
            fullName = longName
        )

        // Then
        assertEquals("Should handle long names", 500, client.fullName.length)
    }

    @Test
    fun `VisitClient should support copy with different values`() {
        // Given
        val original = VisitClient(
            id = "client-123",
            fullName = "Original Name"
        )

        // When
        val copied = original.copy(fullName = "Modified Name")

        // Then
        assertEquals("ID should remain the same", original.id, copied.id)
        assertEquals("Modified name", "Modified Name", copied.fullName)
        assertNotEquals("Names should differ", original.fullName, copied.fullName)
    }

    @Test
    fun `VisitClient equals should work correctly`() {
        // Given
        val client1 = VisitClient(
            id = "client-123",
            fullName = "Test Client"
        )

        val client2 = VisitClient(
            id = "client-123",
            fullName = "Test Client"
        )

        // When & Then
        assertEquals("Should be equal", client1, client2)
    }

    @Test
    fun `VisitClient hashCode should be consistent`() {
        // Given
        val client = VisitClient(
            id = "client-123",
            fullName = "Test Client"
        )

        // When
        val hashCode1 = client.hashCode()
        val hashCode2 = client.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }

    @Test
    fun `VisitClient should handle special characters in name`() {
        // Given
        val specialName = "Test Client @#$%^&*()_+-=[]{}|;':\",./<>?"

        // When
        val client = VisitClient(
            id = "client-123",
            fullName = specialName
        )

        // Then
        assertEquals("Should handle special characters", specialName, client.fullName)
    }

    @Test
    fun `VisitClient should handle different ID formats`() {
        // Given
        val ids = listOf("123", "client-123", "abc-def-ghi", "12345678901234567890")

        // When & Then
        ids.forEach { id ->
            val client = VisitClient(
                id = id,
                fullName = "Test Client"
            )
            assertEquals("ID should match", id, client.id)
        }
    }

    @Test
    fun `VisitClient should handle unicode characters in name`() {
        // Given
        val unicodeName = "José García López 日本語"

        // When
        val client = VisitClient(
            id = "client-123",
            fullName = unicodeName
        )

        // Then
        assertEquals("Should handle unicode characters", unicodeName, client.fullName)
    }

    @Test
    fun `VisitClient should create multiple instances`() {
        // Given & When
        val client1 = VisitClient("client-1", "Client One")
        val client2 = VisitClient("client-2", "Client Two")
        val client3 = VisitClient("client-3", "Client Three")

        // Then
        assertNotEquals("Should be different instances", client1, client2)
        assertNotEquals("Should be different instances", client2, client3)
        assertNotEquals("Should be different instances", client1, client3)
    }
}

