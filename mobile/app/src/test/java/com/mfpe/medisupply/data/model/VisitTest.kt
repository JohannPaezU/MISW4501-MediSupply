package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test

class VisitTest {

    @Test
    fun `Visit should have correct properties`() {
        // Given
        val id = "visit-123"
        val expectedDate = "2025-10-28"
        val visitDate = "2025-10-29"
        val observations = "Test observations"
        val visualEvidence = "https://example.com/image.jpg"
        val visitGeolocation = "4.6097,-74.0817"
        val status = "completed"
        val client = VisitClient(
            id = "client-123",
            name = "Test Client",
            geolocation = "4.6097,-74.0817"
        )

        // When
        val visit = Visit(
            id = id,
            expectedDate = expectedDate,
            visitDate = visitDate,
            observations = observations,
            visualEvidence = visualEvidence,
            visitGeolocation = visitGeolocation,
            status = status,
            client = client
        )

        // Then
        assertEquals("ID should match", id, visit.id)
        assertEquals("Expected date should match", expectedDate, visit.expectedDate)
        assertEquals("Visit date should match", visitDate, visit.visitDate)
        assertEquals("Observations should match", observations, visit.observations)
        assertEquals("Visual evidence should match", visualEvidence, visit.visualEvidence)
        assertEquals("Visit geolocation should match", visitGeolocation, visit.visitGeolocation)
        assertEquals("Status should match", status, visit.status)
        assertEquals("Client should match", client, visit.client)
    }

    @Test
    fun `Visit should be Serializable`() {
        // Given
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "completed",
            client = VisitClient("client-123", "Test Client", "4.6097,-74.0817")
        )

        // When & Then
        assertTrue("Should be Serializable", visit is java.io.Serializable)
    }

    @Test
    fun `Visit should handle null visitDate`() {
        // Given & When
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = null,
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "pending",
            client = VisitClient("client-123", "Test Client", "4.6097,-74.0817")
        )

        // Then
        assertNull("Visit date should be null", visit.visitDate)
        assertEquals("Status should be pending", "pending", visit.status)
    }

    @Test
    fun `Visit should handle empty strings`() {
        // Given & When
        val visit = Visit(
            id = "",
            expectedDate = "",
            visitDate = "",
            observations = "",
            visualEvidence = "",
            visitGeolocation = "",
            status = "",
            client = VisitClient("", "", "")
        )

        // Then
        assertTrue("ID should be empty", visit.id.isEmpty())
        assertTrue("Expected date should be empty", visit.expectedDate.isEmpty())
        assertTrue("Visit date should be empty", visit.visitDate?.isEmpty() == true)
        assertTrue("Observations should be empty", visit.observations.isEmpty())
    }

    @Test
    fun `Visit should handle different statuses`() {
        // Given
        val statuses = listOf("completed", "pending", "cancelled", "in_progress")
        val client = VisitClient("client-123", "Test Client", "4.6097,-74.0817")

        // When & Then
        statuses.forEach { status ->
            val visit = Visit(
                id = "visit-123",
                expectedDate = "2025-10-28",
                visitDate = "2025-10-29",
                observations = "Test",
                visualEvidence = "https://example.com/image.jpg",
                visitGeolocation = "4.6097,-74.0817",
                status = status,
                client = client
            )
            assertEquals("Status should match", status, visit.status)
        }
    }

    @Test
    fun `Visit should support copy with different values`() {
        // Given
        val client = VisitClient("client-123", "Test Client", "4.6097,-74.0817")
        val original = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = null,
            observations = "Original",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "pending",
            client = client
        )

        // When
        val copied = original.copy(
            visitDate = "2025-10-29",
            observations = "Modified",
            status = "completed"
        )

        // Then
        assertEquals("ID should remain the same", original.id, copied.id)
        assertEquals("Modified visit date", "2025-10-29", copied.visitDate)
        assertEquals("Modified observations", "Modified", copied.observations)
        assertEquals("Modified status", "completed", copied.status)
    }

    @Test
    fun `Visit equals should work correctly`() {
        // Given
        val client = VisitClient("client-123", "Test Client", "4.6097,-74.0817")

        val visit1 = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "completed",
            client = client
        )

        val visit2 = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "completed",
            client = client
        )

        // When & Then
        assertEquals("Should be equal", visit1, visit2)
    }

    @Test
    fun `Visit hashCode should be consistent`() {
        // Given
        val client = VisitClient("client-123", "Test Client", "4.6097,-74.0817")
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "completed",
            client = client
        )

        // When
        val hashCode1 = visit.hashCode()
        val hashCode2 = visit.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }

    @Test
    fun `Visit should handle client information correctly`() {
        // Given
        val client = VisitClient(
            id = "client-456",
            name = "Important Client",
            geolocation = "4.6098,-74.0818"
        )

        // When
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidence = "https://example.com/image.jpg",
            visitGeolocation = "4.6097,-74.0817",
            status = "completed",
            client = client
        )

        // Then
        assertEquals("Client ID should match", "client-456", visit.client.id)
        assertEquals("Client name should match", "Important Client", visit.client.name)
        assertEquals("Client geolocation should match", "4.6098,-74.0818", visit.client.geolocation)
    }
}

