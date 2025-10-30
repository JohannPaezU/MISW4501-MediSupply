package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class RegisterVisitResponseTest {

    @Test
    fun `RegisterVisitResponse should have correct properties`() {
        // Given
        val id = "visit-123"
        val clientId = "client-456"
        val expectedDate = Date()
        val visitDate = Date()
        val observations = "Test observations"
        val visualEvidence = "https://example.com/image.jpg"
        val geolocation = "4.6097,-74.0817"
        val status = "completed"

        // When
        val response = RegisterVisitResponse(
            id = id,
            clientId = clientId,
            expectedDate = expectedDate,
            visitDate = visitDate,
            observations = observations,
            visualEvidence = visualEvidence,
            geolocation = geolocation,
            status = status
        )

        // Then
        assertEquals("ID should match", id, response.id)
        assertEquals("Client ID should match", clientId, response.clientId)
        assertEquals("Expected date should match", expectedDate, response.expectedDate)
        assertEquals("Visit date should match", visitDate, response.visitDate)
        assertEquals("Observations should match", observations, response.observations)
        assertEquals("Visual evidence should match", visualEvidence, response.visualEvidence)
        assertEquals("Geolocation should match", geolocation, response.geolocation)
        assertEquals("Status should match", status, response.status)
    }

    @Test
    fun `RegisterVisitResponse should be Serializable`() {
        // Given
        val response = RegisterVisitResponse(
            id = "visit-123",
            clientId = "client-456",
            expectedDate = Date(),
            visitDate = Date(),
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            geolocation = "4.6097,-74.0817",
            status = "completed"
        )

        // When & Then
        assertTrue("Should be Serializable", response is java.io.Serializable)
    }

    @Test
    fun `RegisterVisitResponse should handle empty strings`() {
        // Given & When
        val response = RegisterVisitResponse(
            id = "",
            clientId = "",
            expectedDate = Date(),
            visitDate = Date(),
            observations = "",
            visualEvidence = "",
            geolocation = "",
            status = ""
        )

        // Then
        assertTrue("ID should be empty", response.id.isEmpty())
        assertTrue("Client ID should be empty", response.clientId.isEmpty())
        assertTrue("Observations should be empty", response.observations.isEmpty())
        assertTrue("Visual evidence should be empty", response.visualEvidence.isEmpty())
        assertTrue("Geolocation should be empty", response.geolocation.isEmpty())
        assertTrue("Status should be empty", response.status.isEmpty())
    }

    @Test
    fun `RegisterVisitResponse should handle different statuses`() {
        // Given
        val statuses = listOf("completed", "pending", "cancelled", "in_progress")

        // When & Then
        statuses.forEach { status ->
            val response = RegisterVisitResponse(
                id = "visit-123",
                clientId = "client-456",
                expectedDate = Date(),
                visitDate = Date(),
                observations = "Test",
                visualEvidence = "https://example.com/image.jpg",
                geolocation = "4.6097,-74.0817",
                status = status
            )
            assertEquals("Status should match", status, response.status)
        }
    }

    @Test
    fun `RegisterVisitResponse should support copy with different values`() {
        // Given
        val original = RegisterVisitResponse(
            id = "visit-123",
            clientId = "client-456",
            expectedDate = Date(),
            visitDate = Date(),
            observations = "Original",
            visualEvidence = "https://example.com/image.jpg",
            geolocation = "4.6097,-74.0817",
            status = "pending"
        )

        // When
        val copied = original.copy(observations = "Modified", status = "completed")

        // Then
        assertEquals("ID should remain the same", original.id, copied.id)
        assertEquals("Modified observations", "Modified", copied.observations)
        assertEquals("Modified status", "completed", copied.status)
        assertNotEquals("Observations should differ", original.observations, copied.observations)
    }

    @Test
    fun `RegisterVisitResponse should handle long observation text`() {
        // Given
        val longText = "A".repeat(1000)

        // When
        val response = RegisterVisitResponse(
            id = "visit-123",
            clientId = "client-456",
            expectedDate = Date(),
            visitDate = Date(),
            observations = longText,
            visualEvidence = "https://example.com/image.jpg",
            geolocation = "4.6097,-74.0817",
            status = "completed"
        )

        // Then
        assertEquals("Should handle long text", 1000, response.observations.length)
    }

    @Test
    fun `RegisterVisitResponse equals should work correctly`() {
        // Given
        val date1 = Date()
        val date2 = Date()

        val response1 = RegisterVisitResponse(
            id = "visit-123",
            clientId = "client-456",
            expectedDate = date1,
            visitDate = date2,
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            geolocation = "4.6097,-74.0817",
            status = "completed"
        )

        val response2 = RegisterVisitResponse(
            id = "visit-123",
            clientId = "client-456",
            expectedDate = date1,
            visitDate = date2,
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            geolocation = "4.6097,-74.0817",
            status = "completed"
        )

        // When & Then
        assertEquals("Should be equal", response1, response2)
    }

    @Test
    fun `RegisterVisitResponse hashCode should be consistent`() {
        // Given
        val date1 = Date()
        val date2 = Date()

        val response = RegisterVisitResponse(
            id = "visit-123",
            clientId = "client-456",
            expectedDate = date1,
            visitDate = date2,
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            geolocation = "4.6097,-74.0817",
            status = "completed"
        )

        // When
        val hashCode1 = response.hashCode()
        val hashCode2 = response.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }
}

