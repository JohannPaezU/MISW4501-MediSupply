package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class RegisterVisitResponseTest {

    @Test
    fun `RegisterVisitResponse should have correct properties`() {
        // Given
        val id = "visit-123"
        val client = VisitClient(id = "client-456", fullName = "John Doe")
        val expectedDate = Date()
        val visitDate = Date()
        val observations = "Test observations"
        val visualEvidenceUrl = "https://example.com/image.jpg"
        val expectedGeoLocation = VisitGeolocation(
            id = "geo-1",
            address = "123 Main St",
            latitude = 4.6097,
            longitude = -74.0817
        )
        val reportGeoLocation = VisitGeolocation(
            id = "geo-2",
            address = "123 Main St",
            latitude = 4.6097,
            longitude = -74.0817
        )
        val status = "completed"

        // When
        val response = RegisterVisitResponse(
            id = id,
            clientId = client,
            expectedDate = expectedDate,
            visitDate = visitDate,
            observations = observations,
            visualEvidenceUrl = visualEvidenceUrl,
            expectedGeoLocation = expectedGeoLocation,
            reportGeoLocation = reportGeoLocation,
            status = status
        )

        // Then
        assertEquals("ID should match", id, response.id)
        assertEquals("Client should match", client, response.clientId)
        assertEquals("Expected date should match", expectedDate, response.expectedDate)
        assertEquals("Visit date should match", visitDate, response.visitDate)
        assertEquals("Observations should match", observations, response.observations)
        assertEquals("Visual evidence URL should match", visualEvidenceUrl, response.visualEvidenceUrl)
        assertEquals("Expected geolocation should match", expectedGeoLocation, response.expectedGeoLocation)
        assertEquals("Report geolocation should match", reportGeoLocation, response.reportGeoLocation)
        assertEquals("Status should match", status, response.status)
    }

    @Test
    fun `RegisterVisitResponse should be Serializable`() {
        // Given
        val response = RegisterVisitResponse(
            id = "visit-123",
            clientId = VisitClient(id = "client-456", fullName = "John Doe"),
            expectedDate = Date(),
            visitDate = Date(),
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            expectedGeoLocation = VisitGeolocation(
                id = "geo-1",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            reportGeoLocation = VisitGeolocation(
                id = "geo-2",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
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
            clientId = VisitClient(id = "", fullName = ""),
            expectedDate = Date(),
            visitDate = Date(),
            observations = "",
            visualEvidenceUrl = "",
            expectedGeoLocation = VisitGeolocation(
                id = "",
                address = "",
                latitude = 0.0,
                longitude = 0.0
            ),
            reportGeoLocation = VisitGeolocation(
                id = "",
                address = "",
                latitude = 0.0,
                longitude = 0.0
            ),
            status = ""
        )

        // Then
        assertTrue("ID should be empty", response.id.isEmpty())
        assertTrue("Client ID should be empty", response.clientId.id.isEmpty())
        assertTrue("Observations should be empty", response.observations.isEmpty())
        assertTrue("Visual evidence URL should be empty", response.visualEvidenceUrl.isEmpty())
        assertTrue("Expected geolocation ID should be empty", response.expectedGeoLocation.id.isEmpty())
        assertTrue("Report geolocation ID should be empty", response.reportGeoLocation.id.isEmpty())
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
                clientId = VisitClient(id = "client-456", fullName = "John Doe"),
                expectedDate = Date(),
                visitDate = Date(),
                observations = "Test",
                visualEvidenceUrl = "https://example.com/image.jpg",
                expectedGeoLocation = VisitGeolocation(
                    id = "geo-1",
                    address = "123 Main St",
                    latitude = 4.6097,
                    longitude = -74.0817
                ),
                reportGeoLocation = VisitGeolocation(
                    id = "geo-2",
                    address = "123 Main St",
                    latitude = 4.6097,
                    longitude = -74.0817
                ),
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
            clientId = VisitClient(id = "client-456", fullName = "John Doe"),
            expectedDate = Date(),
            visitDate = Date(),
            observations = "Original",
            visualEvidenceUrl = "https://example.com/image.jpg",
            expectedGeoLocation = VisitGeolocation(
                id = "geo-1",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            reportGeoLocation = VisitGeolocation(
                id = "geo-2",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
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
            clientId = VisitClient(id = "client-456", fullName = "John Doe"),
            expectedDate = Date(),
            visitDate = Date(),
            observations = longText,
            visualEvidenceUrl = "https://example.com/image.jpg",
            expectedGeoLocation = VisitGeolocation(
                id = "geo-1",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            reportGeoLocation = VisitGeolocation(
                id = "geo-2",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
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
            clientId = VisitClient(id = "client-456", fullName = "John Doe"),
            expectedDate = date1,
            visitDate = date2,
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            expectedGeoLocation = VisitGeolocation(
                id = "geo-1",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            reportGeoLocation = VisitGeolocation(
                id = "geo-2",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            status = "completed"
        )

        val response2 = RegisterVisitResponse(
            id = "visit-123",
            clientId = VisitClient(id = "client-456", fullName = "John Doe"),
            expectedDate = date1,
            visitDate = date2,
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            expectedGeoLocation = VisitGeolocation(
                id = "geo-1",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            reportGeoLocation = VisitGeolocation(
                id = "geo-2",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
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
            clientId = VisitClient(id = "client-456", fullName = "John Doe"),
            expectedDate = date1,
            visitDate = date2,
            observations = "Test",
            visualEvidenceUrl = "https://example.com/image.jpg",
            expectedGeoLocation = VisitGeolocation(
                id = "geo-1",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            reportGeoLocation = VisitGeolocation(
                id = "geo-2",
                address = "123 Main St",
                latitude = 4.6097,
                longitude = -74.0817
            ),
            status = "completed"
        )

        // When
        val hashCode1 = response.hashCode()
        val hashCode2 = response.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }
}

