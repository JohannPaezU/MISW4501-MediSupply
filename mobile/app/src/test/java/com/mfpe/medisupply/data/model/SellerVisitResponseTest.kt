package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test

class SellerVisitResponseTest {

    @Test
    fun `SellerVisitResponse should have correct properties`() {
        // Given
        val totalVisits = 5
        val client = VisitClient("client-123", "Test Client")
        val geolocation1 = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val geolocation2 = VisitGeolocation("geo-2", "456 Oak St", 4.6098, -74.0818)
        val visits = listOf(
            Visit(
                id = "visit-1",
                expectedDate = "2025-10-28",
                visitDate = "2025-10-28",
                observations = "Visit 1",
                visualEvidenceUrl = "https://example.com/image1.jpg",
                expectedGeolocation = geolocation1,
                reportGeolocation = geolocation1,
                status = "completed",
                client = client
            ),
            Visit(
                id = "visit-2",
                expectedDate = "2025-10-28",
                visitDate = null,
                observations = "Visit 2",
                visualEvidenceUrl = "https://example.com/image2.jpg",
                expectedGeolocation = geolocation2,
                reportGeolocation = geolocation2,
                status = "pending",
                client = client
            )
        )

        // When
        val response = SellerVisitResponse(
            totalCount = totalVisits,
            visits = visits
        )

        // Then
        assertEquals("Total visits should match", totalVisits, response.totalCount)
        assertEquals("Visits list should match", visits, response.visits)
        assertEquals("Visits count should match", 2, response.visits.size)
    }

    @Test
    fun `SellerVisitResponse should be Serializable`() {
        // Given
        val response = SellerVisitResponse(
            totalCount = 0,
            visits = emptyList()
        )

        // When & Then
        assertTrue("Should be Serializable", response is java.io.Serializable)
    }

    @Test
    fun `SellerVisitResponse should handle empty visits list`() {
        // Given & When
        val response = SellerVisitResponse(
            totalCount = 0,
            visits = emptyList()
        )

        // Then
        assertTrue("Visits list should be empty", response.visits.isEmpty())
        assertEquals("Total visits should be 0", 0, response.totalCount)
    }

    @Test
    fun `SellerVisitResponse should handle large visits list`() {
        // Given
        val client = VisitClient("client-123", "Test Client")
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visits = (1..100).map { index ->
            Visit(
                id = "visit-$index",
                expectedDate = "2025-10-28",
                visitDate = "2025-10-28",
                observations = "Visit $index",
                visualEvidenceUrl = "https://example.com/image$index.jpg",
                expectedGeolocation = geolocation,
                reportGeolocation = geolocation,
                status = "completed",
                client = client
            )
        }

        // When
        val response = SellerVisitResponse(
            totalCount = 100,
            visits = visits
        )

        // Then
        assertEquals("Should handle 100 visits", 100, response.visits.size)
        assertEquals("Total visits should be 100", 100, response.totalCount)
    }

    @Test
    fun `SellerVisitResponse should support copy with different values`() {
        // Given
        val original = SellerVisitResponse(
            totalCount = 5,
            visits = emptyList()
        )

        // When
        val copied = original.copy(totalCount = 10)

        // Then
        assertEquals("Modified total visits", 10, copied.totalCount)
        assertNotEquals("Total visits should differ", original.totalCount, copied.totalCount)
    }

    @Test
    fun `SellerVisitResponse equals should work correctly`() {
        // Given
        val client = VisitClient("client-123", "Test Client")
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visits = listOf(
            Visit(
                id = "visit-1",
                expectedDate = "2025-10-28",
                visitDate = "2025-10-28",
                observations = "Test",
                visualEvidenceUrl = "https://example.com/image.jpg",
                expectedGeolocation = geolocation,
                reportGeolocation = geolocation,
                status = "completed",
                client = client
            )
        )

        val response1 = SellerVisitResponse(
            totalCount = 1,
            visits = visits
        )

        val response2 = SellerVisitResponse(
            totalCount = 1,
            visits = visits
        )

        // When & Then
        assertEquals("Should be equal", response1, response2)
    }

    @Test
    fun `SellerVisitResponse hashCode should be consistent`() {
        // Given
        val response = SellerVisitResponse(
            totalCount = 5,
            visits = emptyList()
        )

        // When
        val hashCode1 = response.hashCode()
        val hashCode2 = response.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }

    @Test
    fun `SellerVisitResponse should handle mismatch between totalCount and visits size`() {
        // Given
        val client = VisitClient("client-123", "Test Client")
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visits = listOf(
            Visit(
                id = "visit-1",
                expectedDate = "2025-10-28",
                visitDate = "2025-10-28",
                observations = "Test",
                visualEvidenceUrl = "https://example.com/image.jpg",
                expectedGeolocation = geolocation,
                reportGeolocation = geolocation,
                status = "completed",
                client = client
            )
        )

        // When
        val response = SellerVisitResponse(
            totalCount = 10,
            visits = visits
        )

        // Then
        assertEquals("Total visits should be 10", 10, response.totalCount)
        assertEquals("Actual visits size should be 1", 1, response.visits.size)
        assertNotEquals("Should handle mismatch", response.totalCount, response.visits.size)
    }

    @Test
    fun `SellerVisitResponse should filter visits by status`() {
        // Given
        val client = VisitClient("client-123", "Test Client")
        val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
        val visits = listOf(
            Visit("v1", "2025-10-28", "2025-10-28", "Test1", "url1", "completed", geolocation, geolocation, client),
            Visit("v2", "2025-10-28", null, "Test2", "url2", "pending", geolocation, geolocation, client),
            Visit("v3", "2025-10-28", "2025-10-28", "Test3", "url3", "completed", geolocation, geolocation, client)
        )

        val response = SellerVisitResponse(
            totalCount = 3,
            visits = visits
        )

        // When
        val completedVisits = response.visits.filter { it.status == "completed" }
        val pendingVisits = response.visits.filter { it.status == "pending" }

        // Then
        assertEquals("Should have 2 completed visits", 2, completedVisits.size)
        assertEquals("Should have 1 pending visit", 1, pendingVisits.size)
    }
}

