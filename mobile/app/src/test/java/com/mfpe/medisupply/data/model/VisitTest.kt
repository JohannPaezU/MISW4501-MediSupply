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
        val status = "completed"
        val expectedGeolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val reportGeolocation = VisitGeolocation("geo-2", "123 Main St", 4.6097, -74.0817)
        val client = VisitClient(
            id = "client-123",
            fullName = "Test Client"
        )

        // When
        val visit = Visit(
            id = id,
            expectedDate = expectedDate,
            visitDate = visitDate,
            observations = observations,
            visualEvidenceUrl = visualEvidence,
            status = status,
            expectedGeolocation = expectedGeolocation,
            reportGeolocation = reportGeolocation,
            client = client
        )

        // Then
        assertEquals("ID should match", id, visit.id)
        assertEquals("Expected date should match", expectedDate, visit.expectedDate)
        assertEquals("Visit date should match", visitDate, visit.visitDate)
        assertEquals("Observations should match", observations, visit.observations)
        assertEquals("Visual evidence should match", visualEvidence, visit.visualEvidenceUrl)
        assertEquals("Expected geolocation should match", expectedGeolocation, visit.expectedGeolocation)
        assertEquals("Report geolocation should match", reportGeolocation, visit.reportGeolocation)
        assertEquals("Status should match", status, visit.status)
        assertEquals("Client should match", client, visit.client)
    }

    @Test
    fun `Visit should be Serializable`() {
        // Given
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            status = "completed",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = VisitClient("client-123", "Test Client")
        )

        // When & Then
        assertTrue("Should be Serializable", visit is java.io.Serializable)
    }

    @Test
    fun `Visit should handle null visitDate`() {
        // Given & When
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = null,
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            status = "pending",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = VisitClient("client-123", "Test Client")
        )

        // Then
        assertNull("Visit date should be null", visit.visitDate)
        assertEquals("Status should be pending", "pending", visit.status)
    }

    @Test
    fun `Visit should handle empty strings`() {
        // Given & When
        val geolocation = VisitGeolocation("", "", 0.0, 0.0)
        val visit = Visit(
            id = "",
            expectedDate = "",
            visitDate = "",
            observations = "",
            visualEvidenceUrl = "",
            status = "",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = VisitClient("", "")
        )

        // Then
        assertTrue("ID should be empty", visit.id.isEmpty())
        assertTrue("Expected date should be empty", visit.expectedDate.isEmpty())
        assertTrue("Visit date should be empty", visit.visitDate?.isEmpty() == true)
        assertTrue("Observations should be empty", visit.observations.isEmpty())
        assertTrue("Visual evidence should be empty", visit.visualEvidenceUrl.isEmpty())
        assertTrue("Status should be empty", visit.status.isEmpty())
    }

    @Test
    fun `Visit should handle different status values`() {
        // Given
        val statuses = listOf("completed", "pending", "cancelled", "in_progress")
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val client = VisitClient("client-123", "Test Client")

        // When & Then
        statuses.forEach { status ->
            val visit = Visit(
                id = "visit-123",
                expectedDate = "2025-10-28",
                visitDate = "2025-10-28",
                observations = "Test",
                visualEvidenceUrl = "https://example.com/image.jpg",
                status = status,
                expectedGeolocation = geolocation,
                reportGeolocation = geolocation,
                client = client
            )
            assertEquals("Status should match", status, visit.status)
        }
    }

    @Test
    fun `Visit should support copy with different values`() {
        // Given
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val original = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = null,
            observations = "Original",
            visualEvidenceUrl = "https://example.com/original.jpg",
            status = "pending",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = VisitClient("client-123", "Test Client")
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
        assertNotEquals("Observations should differ", original.observations, copied.observations)
    }

    @Test
    fun `Visit equals should work correctly`() {
        // Given
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val client = VisitClient("client-123", "Test Client")

        val visit1 = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            status = "completed",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = client
        )

        val visit2 = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            status = "completed",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = client
        )

        // When & Then
        assertEquals("Should be equal", visit1, visit2)
    }

    @Test
    fun `Visit hashCode should be consistent`() {
        // Given
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            status = "completed",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = VisitClient("client-123", "Test Client")
        )

        // When
        val hashCode1 = visit.hashCode()
        val hashCode2 = visit.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }

    @Test
    fun `Visit should handle long observation text`() {
        // Given
        val longText = "A".repeat(2000)
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)

        // When
        val visit = Visit(
            id = "visit-123",
            expectedDate = "2025-10-28",
            visitDate = "2025-10-29",
            observations = longText,
            visualEvidenceUrl = "https://example.com/image.jpg",
            status = "completed",
            expectedGeolocation = geolocation,
            reportGeolocation = geolocation,
            client = VisitClient("client-123", "Test Client")
        )

        // Then
        assertEquals("Should handle long text", 2000, visit.observations.length)
    }
}

